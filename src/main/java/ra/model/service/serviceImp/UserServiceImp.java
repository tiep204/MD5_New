package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import ra.Excreption.RegisterException;
import ra.model.entity.ERole;
import ra.model.entity.PasswordResetToken;
import ra.model.entity.Roles;
import ra.model.entity.Users;
import ra.model.repository.ForgotPassRepository;
import ra.model.repository.UserRepository;
import ra.model.service.RoleService;
import ra.model.service.UserService;
import ra.payload.request.SignupRequest;
import ra.payload.request.UserUpdateRequest;
import ra.payload.response.MessageResponse;
import ra.payload.response.UserResponse;
import ra.security.CustomUserDetails;
import ra.security.CustomUserDetailsService;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ForgotPassRepository forgotPassRepository;
    @Autowired
    private MailService mailService;

    @Override
    public Users findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean exitsByPhoneNumber(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public Users saveOrUpdate(Users user) {
        return userRepository.save(user);
    }

    @Override
    public List<Users> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Users> searchUserByName(String name) {
        return userRepository.searchUsersByUserNameContaining(name);
    }

    @Override
    public Users getUserByID(int userID) {
        return userRepository.getUsersByUserId(userID);
    }

    @Override
    public void register(SignupRequest signupRequest) throws EntityExistsException {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String phoneRegex = "^\\d{10}$";
        String passwordRegex = "^.{6,}$";
        if (existsByUserName(signupRequest.getUsername())) {
            throw new EntityExistsException("Error: username đã tồn tại");
        }
        if (existsByEmail(signupRequest.getEmail())) {
            throw new EntityExistsException("Error: Email đã tồn tại");
        }
        if (exitsByPhoneNumber(signupRequest.getPhone())){
            throw new EntityExistsException("Error: số điện thoại của bạn đã tồn tại");
        }
        if (!signupRequest.getEmail().matches(emailRegex)) {
            throw new EntityExistsException("Error: Định dạng email không hợp lệ");
        }

        if (!signupRequest.getPhone().matches(phoneRegex)) {
            throw new EntityExistsException("Error: Định dạng số điện thoại không hợp lệ");
        }
        if (!signupRequest.getPassword().matches(passwordRegex)) {
            throw new EntityExistsException("Error: Mật khẩu phải có ít nhất 6 ký tự");
        }
        Users user = new Users();
        user.setUserName(signupRequest.getUsername());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setUserStatus(true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        String strNow = sdf.format(dateNow);
        try {
            user.setCreated(sdf.parse(strNow));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Set<String> strRoles = signupRequest.getListRoles();
        Set<Roles> listRoles = new HashSet<>();
        if (strRoles == null) {
            //User quyen mac dinh
            Roles userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            listRoles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Roles adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                    listRoles.add(adminRole);
                }
            });
        }
        user.setListRoles(listRoles);
        saveOrUpdate(user);
    }

    @Override
    public UserResponse myAccount() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = getUserByID(userDetails.getUserId());
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(users.getUserId());
        userResponse.setUserName(users.getUserName());
        userResponse.setCreated(users.getCreated());
        userResponse.setPhone(users.getPhone());
        userResponse.setAddress(users.getAddress());
        userResponse.setEmail(users.getEmail());
        return userResponse;
    }

    @Override
    public Users updateUserInfo(UserUpdateRequest userUpdateRequest, int id) {
        Users users = getUserByID(id);
        users.setEmail(userUpdateRequest.getEmail());
        users.setAddress(userUpdateRequest.getAddress());
        users.setPhone(userUpdateRequest.getPhone());
        return saveOrUpdate(users);
    }

    @Override
    public String message(SignupRequest signupRequest) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String phoneRegex = "^\\d{10}$";
        String passwordRegex = "^.{6,}$";
        if (existsByUserName(signupRequest.getUsername())) {
            throw new EntityExistsException("Error: username đã tồn tại");
        }
        if (existsByEmail(signupRequest.getEmail())) {
            throw new EntityExistsException("Error: Email đã tồn tại");
        }
        if (exitsByPhoneNumber(signupRequest.getPhone())){
            throw new EntityExistsException("Error: số điện thoại của bạn đã tồn tại");
        }
        if (!signupRequest.getEmail().matches(emailRegex)) {
            throw new EntityExistsException("Error: Định dạng email không hợp lệ");
        }

        if (!signupRequest.getPhone().matches(phoneRegex)) {
            throw new EntityExistsException("Error: Định dạng số điện thoại không hợp lệ");
        }
        if (!signupRequest.getPassword().matches(passwordRegex)) {
            throw new EntityExistsException("Error: Mật khẩu phải có ít nhất 6 ký tự");
        }
        return null;
    }

    @Override
    public boolean forgotPassword(String userEmail, HttpServletRequest request) {
        // Kiểm tra xem người dùng tồn tại trong hệ thống dựa trên địa chỉ email
        if (existsByEmail(userEmail)) {
            // Lấy thông tin người dùng dựa trên địa chỉ email
            Users users = findByEmail(userEmail);
            // Tải thông tin người dùng chi tiết (UserDetails) dựa trên tên người dùng (username)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(users.getUserName());
            // Tạo một đối tượng để đại diện cho việc xác thực người dùng
            // và đặt thông tin chi tiết về yêu cầu xác thực từ đối tượng HttpServletRequest
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Đặt thông tin xác thực của người dùng vào SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Tạo một mã thông báo (token) ngẫu nhiên
            String token = UUID.randomUUID().toString();
            // Tạo một đối tượng PasswordResetToken và đặt giá trị mã thông báo cho nó
            PasswordResetToken myToken = new PasswordResetToken();
            // Đặt người dùng liên quan đến mã thông báo
            myToken.setToken(token);
            // Gửi email chứa mã thông báo đến địa chỉ email của người dùng
            String mess = "token is valid for 5 minutes.\n" + "Your token: " + token;
            myToken.setUsers(users);
            // Lấy thời gian hiện tại
            Date now = new Date();
            // Đặt thời gian bắt đầu của mã thông báo
            myToken.setStartDate(now);
            // Lưu hoặc cập nhật mã thông báo trong cơ sở dữ liệu
            saveOrUpdate(myToken);
            mailService.sendSimpleMessage(users.getEmail(),
                    "Reset your password", mess);
            // Trả về true để chỉ ra rằng quá trình đã hoàn tất thành công
            return true;
        } else {
            // Nếu không tìm thấy người dùng, trả về false
            return false;
        }
    }

    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public PasswordResetToken saveOrUpdate(PasswordResetToken passwordResetToken) {
        return forgotPassRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken getLastTokenByUserId(int userId) {
        return forgotPassRepository.getLastTokenByUserId(userId);
    }

    @Override
    public int changePassword(String token, String newPassword) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PasswordResetToken passwordResetToken = getLastTokenByUserId(userDetails.getUserId());
        long date1 = passwordResetToken.getStartDate().getTime() + 1800000;
        long date2 = new Date().getTime();
        if (date2 > date1) {
            return 1;
        } else {
            if (passwordResetToken.getToken().equals(token)) {
                Users users = findByUserId(userDetails.getUserId());
                users.setPassword(encoder.encode(newPassword));
                saveOrUpdate(users);
                return 2;
            } else {
                return 3;
            }
        }
    }

    @Override
    public Users findByUserId(int userID) {
        return userRepository.findById(userID).get();
    }
}
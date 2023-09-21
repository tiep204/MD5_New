package ra.controller;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ra.Excreption.RegisterException;
import ra.jwt.JwtTokenProvider;
import ra.model.entity.Users;
import ra.model.service.ProductService;
import ra.model.service.RoleService;
import ra.model.service.UserService;
import ra.model.service.serviceImp.MailService;
import ra.payload.request.ChangePasswordRequest;
import ra.payload.request.LoginRequest;
import ra.payload.request.SignupRequest;
import ra.payload.request.UserUpdateRequest;
import ra.payload.response.JwtResponse;
import ra.payload.response.MessageResponse;
import ra.payload.response.UserResponse;
import ra.security.CustomUserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    ProductService productService;
    @Autowired
    private MailService mailService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> getAll() {
        return userService.getAll();
    }

    @GetMapping("/lock/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> lock(@PathVariable("userId") int userId) {
        Users users = userService.getUserByID(userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals(users.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Bạn không thể tự khóa chính mình"));
        }
        users.setUserStatus(false);
        userService.saveOrUpdate(users);
        return ResponseEntity.ok(new MessageResponse("Block thành công"));
    }

    @GetMapping("/unlock/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unlock(@PathVariable("userId") int userId) {
        Users users = userService.getUserByID(userId);
        users.setUserStatus(true);
        userService.saveOrUpdate(users);
        return ResponseEntity.ok(new MessageResponse("Unlock User successfully"));
    }

    @GetMapping("/search/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> searchByName(@PathVariable("username") String username) {
        return userService.searchUserByName(username);
    }

/*    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userService.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already"));
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
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
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }  */

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest){
        /*String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String phoneRegex = "^\\d{10}$";
        String passwordRegex = "^.{6,}$";
        if (userService.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: username đã tồn tại"));
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email đã tồn tại"));
        }
        if (userService.exitsByPhoneNumber(signupRequest.getPhone())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: số điện thoại của bạn đã tồn tại"));
        }
        if (!signupRequest.getEmail().matches(emailRegex)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Định dạng email không hợp lệ"));
        }

        if (!signupRequest.getPhone().matches(phoneRegex)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Định dạng số điện thoại không hợp lệ"));
        }

        if (!signupRequest.getPassword().matches(passwordRegex)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Mật khẩu phải có ít nhất 6 ký tự"));
        }*/
            userService.register(signupRequest);
            String emailContent = "<p style=\"color: blue; font-size: 16px;\">Bạn đã đăng ký thành công</p>";
            mailService.sendMail(signupRequest.getEmail(),"RegisterSuccess",emailContent);
            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Users users = userService.findByUserName(loginRequest.getUsername());
        if (users!=null){
            if (users.isUserStatus()) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
                //Sinh JWT tra ve client
                String jwt = tokenProvider.generateToken(customUserDetail);
                //Lay cac quyen cua user
                List<String> listRoles = customUserDetail.getAuthorities().stream()
                        .map(item -> item.getAuthority()).collect(Collectors.toList());
                return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getEmail(),
                        customUserDetail.getPhone(), customUserDetail.getAddress(), listRoles));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Tài khoản của bạn đã bị khóa"));
            }
        }else {
            return ResponseEntity.badRequest().body(new MessageResponse("Tên đăng nhập hoặc tài khoản của bạn không chính xác"));
        }
    }

/*    @PutMapping("/updateUserInfo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Users users = userService.getUserByID(userDetails.getUserId());
            users.setEmail(userUpdateRequest.getEmail());
            users.setAddress(userUpdateRequest.getAddress());
            users.setPhone(userUpdateRequest.getPhone());
            userService.saveOrUpdate(users);
            return ResponseEntity.ok(new MessageResponse("Update thanh cong"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }*/

    @PutMapping("/updateUserInfo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userService.updateUserInfo(userUpdateRequest,userDetails.getUserId());
            return ResponseEntity.ok(new MessageResponse("Update thanh cong"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

    @PutMapping("/changePassword")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.getUserByID(userDetails.getUserId());
        // Kiểm tra mật khẩu cũ
        if (encoder.matches(changePasswordRequest.getOldPassword(), users.getPassword())) {
            // Kiểm tra xác nhận mật khẩu mới
            if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
                // Mật khẩu mới hợp lệ, mã hóa và lưu trữ trong cơ sở dữ liệu
                users.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
                userService.saveOrUpdate(users);
                return ResponseEntity.ok(new MessageResponse("Mật khẩu đã được thay đổi thành công."));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Mật khẩu mới và xác nhận mật khẩu mới không trùng khớp!"));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Mật khẩu cũ không chính xác!"));
        }
    }

    @GetMapping("/myAccount")
    @PreAuthorize("hasRole('USER')")
    public UserResponse getUser() {
     /*   CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.getUserByID(userDetails.getUserId());
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(users.getUserId());
        userResponse.setUserName(users.getUserName());
        userResponse.setCreated(users.getCreated());
        userResponse.setPhone(users.getPhone());
        userResponse.setAddress(users.getAddress());
        userResponse.setEmail(users.getEmail());*/
        return userService.myAccount();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        request.getHeader("Authorization");
        // Clear the authentication from server-side (in this case, Spring Security)
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Bạn đã đăng xuất");
    }
}
package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
        if (users==null){
            return ResponseEntity.ok(new MessageResponse("User không tồn tại"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals(users.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Bạn không thể tự khóa chính mình"));
        }
        if (users.isUserStatus() == true) {
            users.setUserStatus(false);
            userService.saveOrUpdate(users);
            return ResponseEntity.ok(new MessageResponse("Block thành công"));
        } else if (users.isUserStatus() == false) {
            return ResponseEntity.ok(new MessageResponse("Bạn đã block user này"));
        }
        return ResponseEntity.ok(new MessageResponse("Đã có lỗi trong quá trình sử lý"));
    }

    @GetMapping("/unlock/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unlock(@PathVariable("userId") int userId) {
        Users users = userService.getUserByID(userId);
        if (users.isUserStatus() == false) {
            users.setUserStatus(true);
            userService.saveOrUpdate(users);
            return ResponseEntity.ok(new MessageResponse("Bạn đã unlock user này thành công"));
        } else {
            return ResponseEntity.ok(new MessageResponse("User này chưa được block"));
        }
    }

    @GetMapping("/search/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> searchByName(@PathVariable("username") String username) {
        return userService.searchUserByName(username);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        userService.register(signupRequest);
        String emailContent = "<p style=\"color: blue; font-size: 16px;\">Bạn đã đăng ký thành công</p>";
        mailService.sendMail(signupRequest.getEmail(), "RegisterSuccess", emailContent);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Users users = userService.findByUserName(loginRequest.getUsername());
        if (users != null) {
            if (users.isUserStatus()) {
                try {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
                    // Sinh JWT và trả về cho client
                    String jwt = tokenProvider.generateToken(customUserDetail);
                    // Lấy các quyền của người dùng
                    List<String> listRoles = customUserDetail.getAuthorities().stream()
                            .map(item -> item.getAuthority()).collect(Collectors.toList());
                    return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getEmail(),
                            customUserDetail.getPhone(), customUserDetail.getAddress(), listRoles));
                } catch (BadCredentialsException ex) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Tài khoản hoặc mật khẩu của bạn không chính xác"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Error: Tài khoản của bạn đã bị khóa"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Tên đăng nhập hoặc tài khoản của bạn không chính xác"));
        }
    }
    @PutMapping("/updateUserInfo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userService.updateUserInfo(userUpdateRequest, userDetails.getUserId());
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
                mailService.sendMail(users.getEmail(),"Thông báo","Mật khẩu của bạn được đổi thành công là: "+changePasswordRequest.getNewPassword());
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
    public ResponseEntity<UserResponse>  getUser() {
        return new ResponseEntity<>(userService.myAccount(),HttpStatus.OK) ;
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        request.getHeader("Authorization");
        // Clear the authentication from server-side (in this case, Spring Security)
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Bạn đã đăng xuất");
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam("userEmail") String userEmail, HttpServletRequest request) {
        boolean check = userService.forgotPassword(userEmail, request);
        if (check) {
            return ResponseEntity.ok("Email sent! Please check your email");
        } else {
            return ResponseEntity.ok("Email is not already");
        }
    }

    @PatchMapping("/creatNewPass")
    public ResponseEntity<?> changePassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        int num = userService.changePassword(token, newPassword);
        if (num == 1) {
            return ResponseEntity.ok("token da het han");
        } else if (num == 2) {
            return ResponseEntity.ok("doi mat khau thanh cong");
        } else {
            return ResponseEntity.ok("Ma token khong dung");
        }
    }
}
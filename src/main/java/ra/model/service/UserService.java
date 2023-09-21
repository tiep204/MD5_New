package ra.model.service;

import ra.model.entity.PasswordResetToken;
import ra.model.entity.Users;
import ra.payload.request.SignupRequest;
import ra.payload.request.UserUpdateRequest;
import ra.payload.response.UserResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Users findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean exitsByPhoneNumber(String phone);
    Users saveOrUpdate(Users user);
    List<Users> getAll();
    List<Users> searchUserByName(String name);
    Users getUserByID(int userID);
    void register(SignupRequest signupRequest);
    UserResponse myAccount();
    Users updateUserInfo(UserUpdateRequest userUpdateRequest, int id);
    String message(SignupRequest signupRequest);
    boolean forgotPassword(String userEmail, HttpServletRequest request);
    Users findByEmail(String email);
    PasswordResetToken saveOrUpdate(PasswordResetToken passwordResetToken);
    PasswordResetToken getLastTokenByUserId(int userId);
    int changePassword(String token,String newPassword);
    Users findByUserId(int userID);
}
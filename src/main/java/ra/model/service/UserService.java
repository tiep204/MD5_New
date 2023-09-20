package ra.model.service;

import ra.model.entity.Users;
import ra.payload.request.SignupRequest;
import ra.payload.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Users findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Users saveOrUpdate(Users user);
    List<Users> getAll();
    List<Users> searchUserByName(String name);
    Users getUserByID(int userID);
    void register(SignupRequest signupRequest);
    UserResponse myAccount();
}
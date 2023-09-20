package ra.model.service.mapper;

import org.springframework.stereotype.Component;
import ra.model.entity.Users;
import ra.payload.request.UserUpdateRequest;
import ra.payload.response.UserResponse;
@Component
public class UserMapper implements IGenericMapper<Users, UserUpdateRequest, UserResponse>{
    @Override
    public Users toEntity(UserUpdateRequest userUpdateRequest) {
        return Users.builder()
                .phone(userUpdateRequest.getPhone())
                .email(userUpdateRequest.getEmail())
                .address(userUpdateRequest.getAddress())
                .build();
    }

    @Override
    public UserResponse toResponse(Users users) {
        return UserResponse.builder()
                .userId(users.getUserId())
                .userName(users.getUserName())
                .created(users.getCreated())
                .email(users.getEmail())
                .phone(users.getPhone())
                .address(users.getAddress())
                .build();
    }
}

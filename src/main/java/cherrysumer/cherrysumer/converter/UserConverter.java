package cherrysumer.cherrysumer.converter;

import cherrysumer.cherrysumer.domain.User;
import cherrysumer.cherrysumer.web.dto.UserRequestDTO;
import cherrysumer.cherrysumer.web.dto.UserResponseDTO;

public class UserConverter {

    public static User createUser(UserRequestDTO.userJoinRequestDTO request, String passwd) {
        return User.builder()
                .loginId(request.getLoginId())
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .passwd(passwd)
                .category(request.getCategory())
                .region(request.getRegion())
                .build();
    }

    public static UserRequestDTO.userInfoDTO createUserDTO(User user) {
        return UserRequestDTO.userInfoDTO.builder()
                .nickname(user.getNickname())
                .build();
    }

    public static UserResponseDTO.successLoginDTO successLogin(String token) {
        return UserResponseDTO.successLoginDTO.builder()
                .token(token)
                .build();
    }
}

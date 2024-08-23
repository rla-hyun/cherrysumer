package cherrysumer.cherrysumer.service;

import cherrysumer.cherrysumer.converter.UserConverter;
import cherrysumer.cherrysumer.domain.User;
import cherrysumer.cherrysumer.exception.ErrorCode;
import cherrysumer.cherrysumer.exception.handler.CertificationHandler;
import cherrysumer.cherrysumer.exception.handler.UserErrorHandler;
import cherrysumer.cherrysumer.jwt.TokenProvider;
import cherrysumer.cherrysumer.repository.UserRepository;
import cherrysumer.cherrysumer.web.dto.UserRequestDTO;
import cherrysumer.cherrysumer.web.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void userJoin(UserRequestDTO.userJoinRequestDTO request) {
        String password = hashPassword(request.getPasswd());
        User user = UserConverter.createUser(request, password);
        userRepository.save(user);
    }

    @Override
    public void findUserId(UserRequestDTO.existUserIdRequestDTO request) {
        boolean existUser = userRepository.existsUserByLoginId(request.getLoginId());
        if(existUser)
            throw new UserErrorHandler(ErrorCode._LOGINID_CONFLICT);
    }

    @Override
    public void findNickname(UserRequestDTO.existUserNicknameRequestDTO request) {
        boolean existUser = userRepository.existsUserByNickname(request.getNickname());
        if(existUser)
            throw new UserErrorHandler(ErrorCode._NICKNAME_CONFLICT);
    }

    @Override
    public UserResponseDTO.successLoginDTO userLogin(UserRequestDTO.userLoginRequestDTO request) {
        User user = userRepository.findUserByLoginId(request.getLoginId());
        if(checkPassword(request.getPassword(), user)) {
            String token = tokenProvider.generateJwtToken(UserConverter.createUserDTO(user));
            return UserConverter.successLogin(token);
        }
        throw new UserErrorHandler(ErrorCode._USER_NOT_FOUND);
    }

    /***
     * 비밀번호 암호화
     * @param pwd
     * @return
     */
    private String hashPassword(String pwd) {
        String password = passwordEncoder.encode(pwd);
        return password;
    }

    /***
     * 비밀번호 일치 여부 확인
     * @param pwd 사용자가 입력한 비밀번호
     * @param user user table에 저장된 암호화된 비밀번호
     * @return
     */
    private boolean checkPassword(String pwd, User user) {
        return passwordEncoder.matches(pwd, user.getPasswd());
    }

    @Override
    public String checkToken() {
        // 로그인 아이디 출력됨.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // 토큰에서 유저 데이터 가져오기
    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findUserByLoginId(loginId);
    }
}

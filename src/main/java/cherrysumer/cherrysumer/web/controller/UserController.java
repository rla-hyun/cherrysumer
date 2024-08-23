package cherrysumer.cherrysumer.web.controller;

import cherrysumer.cherrysumer.service.MailService;
import cherrysumer.cherrysumer.service.UserService;
import cherrysumer.cherrysumer.web.dto.MailRequestDTO;
import cherrysumer.cherrysumer.web.dto.UserRequestDTO;
import cherrysumer.cherrysumer.web.dto.UserResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/join")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestDTO.userJoinRequestDTO request) {
        userService.userJoin(request);
        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 성공하였습니다.");
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponseDTO.successLoginDTO> loginUser(@RequestBody UserRequestDTO.userLoginRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.userLogin(request));
    }

    // 아이디 중복 확인
    @GetMapping("/id-exists")
    public ResponseEntity<String> checkExistId(@RequestBody UserRequestDTO.existUserIdRequestDTO request) {
        userService.findUserId(request);
        return ResponseEntity.status(HttpStatus.OK).body("사용가능한 아이디입니다.");
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname-exists")
    public ResponseEntity<String> checkExistNickname(@RequestBody UserRequestDTO.existUserNicknameRequestDTO request) {
        userService.findNickname(request);
        return ResponseEntity.status(HttpStatus.OK).body("사용가능한 닉네임입니다.");
    }

    // 이메일 인증 코드 요청
    @PostMapping("/email-verification")
    public ResponseEntity<String> requestverificationCode(@RequestParam("email") String email) throws NoSuchAlgorithmException, MessagingException {
        mailService.sendCode(email);
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드 전송 완료");
    }

    // 이메일 인증
    @GetMapping("/email-verification")
    public ResponseEntity<String> verificationCode(@RequestBody MailRequestDTO.verificationRequestDTO request) throws NoSuchAlgorithmException {
        mailService.checkCode(request);
        return ResponseEntity.status(HttpStatus.OK).body("이메일 인증 성공");
    }
}

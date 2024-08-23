package cherrysumer.cherrysumer.converter;

import cherrysumer.cherrysumer.domain.MailCode;

import java.time.LocalDateTime;

public class VerificationConverter {

    public static MailCode createVerificationCode(String email, String code) {
        return MailCode.builder()
                .email(email)
                .code(code)
                .status(false)
                .expirtime(LocalDateTime.now().plusMinutes(10))
                .build();

    }
}

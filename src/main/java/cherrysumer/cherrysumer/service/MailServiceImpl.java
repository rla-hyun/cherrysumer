package cherrysumer.cherrysumer.service;

import cherrysumer.cherrysumer.domain.MailCode;
import cherrysumer.cherrysumer.exception.ErrorCode;
import cherrysumer.cherrysumer.exception.handler.CertificationHandler;
import cherrysumer.cherrysumer.jwt.TokenProvider;
import cherrysumer.cherrysumer.repository.MailCodeRepository;
import cherrysumer.cherrysumer.util.MailCodeMessage;
import cherrysumer.cherrysumer.web.dto.MailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    private final MailCodeRepository codeRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void sendMail(String email, String title, String content) {
        MimeMessage verticationMail = createVerticationMail(email, title, content);

        mailSender.send(verticationMail);
    }

    private MimeMessage createVerticationMail(String email, String title, String content){
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mail, true, "UTF-8");
            message.setTo(email);
            message.setSubject(title);
            message.setText(content, true);
        } catch(MessagingException e) {
            e.printStackTrace();
        }

        return mail;
    }

    @Override
    public void sendCode(String email){
        String code = tokenProvider.generatedMailCode();
        MailCode verification;
        if(codeRepository.existsByEmail(email)) {
            verification = codeRepository.findByEmail(email);

            verification.setStatus(false);
            verification.setCode(code);
            verification.setExpirtime(LocalDateTime.now().plusMinutes(10));
        } else {
            verification = new MailCode();

            verification.setEmail(email);
            verification.setCode(code);
            verification.setStatus(false);
            verification.setExpirtime(LocalDateTime.now().plusMinutes(10));
        }

        codeRepository.save(verification);
        sendMail(email, "인증 코드", MailCodeMessage.mailContent(code));
    }

    @Override
    public void checkCode(MailRequestDTO.verificationRequestDTO request) {
        MailCode verification = codeRepository.findByEmail(request.getEmail());

        // 인증 번호 일치 여부 확인
        if (!verification.getCode().equals(request.getCode())) {
            throw new CertificationHandler(ErrorCode._MAIL_INCORRECT);
        }
        // 인증 완료 여부 확인
        if (verification.getStatus()) {
            throw new CertificationHandler(ErrorCode._MAIL_CONFLICT);
        }
        // 인증 시간 만료 여부 확인
        if (verification.getExpirtime().isBefore(LocalDateTime.now())) {
            throw new CertificationHandler(ErrorCode._MAIL_UNAUTHORIZED);
        }

        // 인증 성공, 상태 업데이트
        verification.setStatus(true);
        codeRepository.save(verification);
    }
}

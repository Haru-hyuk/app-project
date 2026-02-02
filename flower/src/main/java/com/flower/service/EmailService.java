package com.flower.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * 임시 비밀번호 이메일 발송
     */
    public void sendTempPasswordEmail(String toEmail, String userName, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 발신자 이메일 검증 및 설정
            String senderEmail = getValidSenderEmail();
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("[플로릿] 임시 비밀번호 발송");

            String htmlContent = buildTempPasswordEmailContent(userName, tempPassword);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("임시 비밀번호 이메일 발송 성공: {}", toEmail);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 유효한 발신자 이메일 주소 반환
     * 설정이 없거나 비어있으면 예외 발생
     */
    private String getValidSenderEmail() {
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            log.error("발신자 이메일이 설정되지 않았습니다. application.yml의 spring.mail.username을 설정해주세요.");
            throw new IllegalStateException("발신자 이메일이 설정되지 않았습니다. application.yml의 spring.mail.username을 설정해주세요.");
        }
        return fromEmail.trim();
    }

    /**
     * 임시 비밀번호 이메일 HTML 내용 생성
     */
    private String buildTempPasswordEmailContent(String userName, String tempPassword) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Malgun Gothic', Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, #9E7AFF 0%, #D1C4FF 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".password-box { background: white; border: 2px solid #9E7AFF; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: center; }" +
                ".password { font-size: 24px; font-weight: bold; color: #9E7AFF; letter-spacing: 2px; }" +
                ".warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px; }" +
                ".footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🌸 플로릿 (Florit)</h1>" +
                "<h2>임시 비밀번호 발송</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>안녕하세요, <strong>" + userName + "</strong>님!</p>" +
                "<p>요청하신 임시 비밀번호를 발송해드립니다.</p>" +
                "<div class='password-box'>" +
                "<p style='margin: 0; color: #666;'>임시 비밀번호</p>" +
                "<p class='password'>" + tempPassword + "</p>" +
                "</div>" +
                "<div class='warning'>" +
                "<p><strong>⚠️ 보안 안내</strong></p>" +
                "<ul style='margin: 10px 0; padding-left: 20px;'>" +
                "<li>로그인 후 반드시 비밀번호를 변경해주세요.</li>" +
                "<li>임시 비밀번호는 타인에게 노출되지 않도록 주의해주세요.</li>" +
                "<li>비밀번호 변경은 마이페이지에서 가능합니다.</li>" +
                "</ul>" +
                "</div>" +
                "<p>임시 비밀번호로 로그인하신 후, 새로운 비밀번호로 변경해주시기 바랍니다.</p>" +
                "<p>감사합니다.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>본 이메일은 자동으로 발송된 메일입니다.</p>" +
                "<p>© 2026 플로릿 (Florit). All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

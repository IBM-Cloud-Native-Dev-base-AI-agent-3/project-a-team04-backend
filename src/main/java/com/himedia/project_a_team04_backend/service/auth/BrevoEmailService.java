package com.himedia.project_a_team04_backend.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrevoEmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    @Value("${app.backend-url}")
    private String backendUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationLink = backendUrl + "/auth/verify?token=" + token;

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "[" + senderName + "] 이메일 인증을 완료해주세요",
                "htmlContent", buildEmailHtml(verificationLink)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        restTemplate.postForEntity(BREVO_API_URL, new HttpEntity<>(body, headers), String.class);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/password-reset?token=" + token;

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "[" + senderName + "] 비밀번호 재설정 안내",
                "htmlContent", buildPasswordResetHtml(resetLink)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        restTemplate.postForEntity(BREVO_API_URL, new HttpEntity<>(body, headers), String.class);
    }

    public void sendForumApplyEmail(String toEmail, String forumTitle) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "[" + senderName + "] 포럼 신청이 접수되었습니다",
                "htmlContent", buildForumApplyHtml(forumTitle)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        restTemplate.postForEntity(BREVO_API_URL, new HttpEntity<>(body, headers), String.class);
    }

    public void sendForumReviewEmail(String toEmail, String forumTitle, boolean accepted, String rejectReason) {
        String subject = accepted
                ? "[" + senderName + "] 포럼 신청이 승인되었습니다"
                : "[" + senderName + "] 포럼 신청이 거절되었습니다";

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "htmlContent", buildForumReviewHtml(forumTitle, accepted, rejectReason)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        restTemplate.postForEntity(BREVO_API_URL, new HttpEntity<>(body, headers), String.class);
    }

    private String buildForumApplyHtml(String forumTitle) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                  <h2>포럼 신청 접수 완료</h2>
                  <p><strong>%s</strong> 포럼 신청이 정상적으로 접수되었습니다.</p>
                  <p>검토 후 결과를 이메일로 안내드리겠습니다.</p>
                </div>
                """.formatted(forumTitle);
    }

    private String buildForumReviewHtml(String forumTitle, boolean accepted, String rejectReason) {
        if (accepted) {
            return """
                    <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                      <h2>포럼 신청 승인</h2>
                      <p><strong>%s</strong> 포럼 신청이 <strong style="color:#16a34a;">승인</strong>되었습니다.</p>
                      <p>포럼 당일 참석해주시기 바랍니다.</p>
                    </div>
                    """.formatted(forumTitle);
        } else {
            return """
                    <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                      <h2>포럼 신청 거절</h2>
                      <p><strong>%s</strong> 포럼 신청이 <strong style="color:#dc2626;">거절</strong>되었습니다.</p>
                      %s
                    </div>
                    """.formatted(forumTitle,
                    rejectReason != null && !rejectReason.isBlank()
                            ? "<p>사유: " + rejectReason + "</p>"
                            : "");
        }
    }

    private String buildPasswordResetHtml(String resetLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                  <h2>비밀번호 재설정</h2>
                  <p>아래 버튼을 클릭하면 비밀번호를 재설정할 수 있습니다.<br>링크는 <strong>30분</strong> 동안 유효합니다.</p>
                  <a href="%s"
                     style="display:inline-block; padding:12px 24px; background:#4f46e5;
                            color:#fff; border-radius:6px; text-decoration:none;">
                    비밀번호 재설정하기
                  </a>
                  <p style="margin-top:16px; color:#6b7280; font-size:13px;">
                    본인이 요청하지 않았다면 이 메일을 무시하세요.
                  </p>
                </div>
                """.formatted(resetLink);
    }

    private String buildEmailHtml(String verificationLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                  <h2>이메일 인증</h2>
                  <p>아래 버튼을 클릭하면 이메일 인증이 완료됩니다. 링크는 <strong>30분</strong> 동안 유효합니다.</p>
                  <a href="%s"
                     style="display:inline-block; padding:12px 24px; background:#4f46e5;
                            color:#fff; border-radius:6px; text-decoration:none;">
                    이메일 인증하기
                  </a>
                  <p style="margin-top:16px; color:#6b7280; font-size:13px;">
                    본인이 요청하지 않았다면 이 메일을 무시하세요.
                  </p>
                </div>
                """.formatted(verificationLink);
    }
}

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

    private String buildEmailHtml(String verificationLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                  <h2>이메일 인증</h2>
                  <p>아래 버튼을 클릭하면 이메일 인증이 완료됩니다.<br>링크는 <strong>30분</strong> 동안 유효합니다.</p>
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

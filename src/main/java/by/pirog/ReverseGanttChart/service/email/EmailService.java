package by.pirog.ReverseGanttChart.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendInvitationEmail(String toEmail, String projectName, String invitationUrl,
                                    String inviterName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject("Приглашение в проект: " + projectName);
            mimeMessageHelper.setText(createInvitationHtml(projectName, inviterName, invitationUrl), true);

            javaMailSender.send(mimeMessage);

            log.info("✅ Приглашение отправлено на: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Ошибка отправки email на {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String resetPasswordUrl) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject("Сброс пароля");
            mimeMessageHelper.setText(createResetPasswordHtml(resetPasswordUrl), true);

            javaMailSender.send(mimeMessage);

            log.info("✅ Reset отправлен на: {}", toEmail);
        } catch (MessagingException e) {
            log.error("❌ Ошибка отправки email на {}: {}", toEmail, e.getMessage());
        }
    }


    private String createInvitationHtml(String projectName, String inviterName,
                                        String invitationUrl) {
        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body { 
                                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                                    line-height: 1.6; 
                                    color: #333; 
                                    max-width: 600px; 
                                    margin: 0 auto; 
                                    padding: 20px; 
                                }
                                .header { 
                                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                    color: white; 
                                    padding: 30px; 
                                    text-align: center; 
                                    border-radius: 10px 10px 0 0; 
                                }
                                .content { 
                                    padding: 30px; 
                                    background: #f9f9f9; 
                                    border: 1px solid #ddd; 
                                    border-top: none; 
                                    border-radius: 0 0 10px 10px; 
                                }
                                .button { 
                                    display: inline-block; 
                                    padding: 14px 28px; 
                                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                    color: white; 
                                    text-decoration: none; 
                                    border-radius: 8px; 
                                    font-weight: bold; 
                                    margin: 20px 0; 
                                    text-align: center; 
                                }
                                .footer { 
                                    margin-top: 30px; 
                                    padding-top: 20px; 
                                    border-top: 1px solid #ddd; 
                                    color: #777; 
                                    font-size: 12px; 
                                    text-align: center; 
                                }
                            </style>
                        </head>
                        <body>
                            <div class="header">
                                <h1>🎯 Приглашение в проект</h1>
                            </div>
                        
                            <div class="content">
                                <h2>Здравствуйте!</h2>
                        
                                <p>Вас пригласили присоединиться к проекту:</p>
                        
                                <div style="background: white; padding: 15px; border-radius: 8px; 
                                     border-left: 4px solid #667eea; margin: 20px 0;">
                                    <p><strong>📋 Проект:</strong> %s</p>
                                    <p><strong>👤 Пригласил:</strong> %s</p>
                                    <p><strong>⏳ Ссылка действует:</strong> 7 дней</p>
                                </div>
                        
                                <p style="text-align: center;">
                                    <a href="%s" class="button">
                                        ✅ Принять приглашение
                                    </a>
                                </p>
                        
                                <p>Или скопируйте ссылку:</p>
                                <code style="background: #f0f0f0; padding: 10px; display: block; 
                                       border-radius: 5px; word-break: break-all;">
                                    %s
                                </code>
                        
                                <p style="color: #666; font-style: italic;">
                                    Если вы не ожидали этого приглашения, просто проигнорируйте это письмо.
                                </p>
                            </div>
                        
                            <div class="footer">
                                <p>Это письмо отправлено автоматически. Пожалуйста, не отвечайте на него.</p>
                                <p>© %d Project Management System</p>
                            </div>
                        </body>
                        </html>
                        """,
                projectName,
                inviterName,
                invitationUrl,
                invitationUrl,
                java.time.LocalDateTime.now().getYear()
        );
    }

    private String createResetPasswordHtml(String resetUrl) {
        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body { 
                                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                                    line-height: 1.6; 
                                    color: #333; 
                                    max-width: 600px; 
                                    margin: 0 auto; 
                                    padding: 20px; 
                                }
                                .header { 
                                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                    color: white; 
                                    padding: 30px; 
                                    text-align: center; 
                                    border-radius: 10px 10px 0 0; 
                                }
                                .content { 
                                    padding: 30px; 
                                    background: #f9f9f9; 
                                    border: 1px solid #ddd; 
                                    border-top: none; 
                                    border-radius: 0 0 10px 10px; 
                                }
                                .button { 
                                    display: inline-block; 
                                    padding: 14px 28px; 
                                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                    color: white !important; 
                                    text-decoration: none; 
                                    border-radius: 8px; 
                                    font-weight: bold; 
                                    margin: 20px 0; 
                                    text-align: center; 
                                }
                                .footer { 
                                    margin-top: 30px; 
                                    padding-top: 20px; 
                                    border-top: 1px solid #ddd; 
                                    color: #777; 
                                    font-size: 12px; 
                                    text-align: center; 
                                }
                                .info-box {
                                    background: white;
                                    padding: 15px;
                                    border-radius: 8px;
                                    border-left: 4px solid #667eea;
                                    margin: 20px 0;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="header">
                                <h1>🔐 Сброс пароля</h1>
                            </div>
                        
                            <div class="content">
                                <h2>Здравствуйте!</h2>
                        
                                <p>Вы получили это письмо, потому что был запрошен сброс пароля для вашей учетной записи.</p>
                        
                                <div class="info-box">
                                    <p><strong>⏳ Ссылка действует:</strong> 30 минут</p>
                                    <p><strong>🔒 Безопасность:</strong> ссылка одноразовая</p>
                                </div>
                        
                                <p style="text-align: center;">
                                    <a href="%s" class="button">
                                        🔑 Сменить пароль
                                    </a>
                                </p>
                        
                                <p>Или скопируйте ссылку:</p>
                                <code style="background: #f0f0f0; padding: 10px; display: block; 
                                       border-radius: 5px; word-break: break-all;">
                                    %s
                                </code>
                        
                                <p style="color: #666; font-style: italic;">
                                    Если вы не запрашивали смену пароля, просто проигнорируйте это письмо.
                                </p>
                            </div>
                        
                            <div class="footer">
                                <p>Это письмо отправлено автоматически. Пожалуйста, не отвечайте на него.</p>
                                <p>© %d ReverseGanttChart</p>
                            </div>
                        </body>
                        </html>
                        """,
                resetUrl,
                resetUrl,
                java.time.LocalDateTime.now().getYear()
        );
    }

}

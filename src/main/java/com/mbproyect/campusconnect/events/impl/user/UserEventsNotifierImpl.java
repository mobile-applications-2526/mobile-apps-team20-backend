package com.mbproyect.campusconnect.events.impl.user;

import com.mbproyect.campusconnect.events.contract.user.UserEventsNotifier;
import com.mbproyect.campusconnect.shared.service.MailService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserEventsNotifierImpl implements UserEventsNotifier {

    private final MailService mailService;

    public UserEventsNotifierImpl(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onUserRegisteredEvent(String email, String activationLink) {
        String body = String.format(
                "Hello,%n%n" +
                        "Welcome to %s! To activate your account, please click the link below:%n%n" +
                        "%s%n%n" +
                        "This link will expire in 24 hours for your security.%n" +
                        "If you didn’t create an account, please ignore this message.%n%n" +
                        "Best regards,%n" +
                        "The %s Support Team",
                "CampusConnect", activationLink, "Campus connect"
        );

        mailService.sendEmail(email, "Account activation", body);

    }

    @Override
    public void onUserLoggedEvent(String email, String verificationCode) {
        String body = String.format("""
            <html>
              <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #222;">
                <p>Hi!,</p>
                <p>Your verification code to access your account is:</p>
                <div style="text-align: center; margin: 20px 0;">
                  <span style="display: inline-block; background-color: #f3f4f6; color: #2d3748;
                               font-size: 28px; letter-spacing: 4px; font-weight: bold;
                               padding: 12px 24px; border-radius: 8px; border: 1px solid #e2e8f0;">
                    <h1>%s</h1>
                  </span>
                </div>
                <p>This code will expire in 10 minutes.</p>
                <p>If you didn’t request this code, please ignore this message.</p>
                <br/>
                <p>Best regards,<br/>
                   <strong>The %s Support Team</strong></p>
              </body>
            </html>
            """, verificationCode, "CampusConnect");

        mailService.sendEmail(email, "Account verification", body);
    }
}

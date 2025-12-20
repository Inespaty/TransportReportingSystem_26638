package com.transport.TransportReportingSystem.util;

public class EmailTemplateUtils {

    private static final String PRIMARY_BLUE = "#1e3a8a"; // deep blue-900/950
    private static final String ACCENT_BLUE = "#2563eb"; // blue-600
    private static final String BG_COLOR = "#f8fafc"; // slate-50

    public static String wrapInLayout(String title, String content) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: " + BG_COLOR + "; margin: 0; padding: 0; color: #1e293b; }" +
                "        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); border: 1px solid #e2e8f0; }" +
                "        .header { background-color: " + PRIMARY_BLUE + "; padding: 40px 20px; text-align: center; }" +
                "        .header h1 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: -0.025em; }" +
                "        .content { padding: 40px; line-height: 1.6; }" +
                "        .footer { background-color: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px; border-top: 1px solid #e2e8f0; }" +
                "        .button { display: inline-block; background-color: " + ACCENT_BLUE + "; color: #ffffff !important; padding: 14px 28px; border-radius: 12px; text-decoration: none; font-weight: 700; margin-top: 24px; box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.4); }" +
                "        .code { display: block; background-color: #f1f5f9; padding: 20px; border-radius: 12px; font-size: 32px; font-weight: 800; letter-spacing: 0.2em; text-align: center; color: " + PRIMARY_BLUE + "; margin: 24px 0; border: 2px dashed #cbd5e1; }" +
                "        .greeting { font-size: 18px; font-weight: 600; margin-bottom: 16px; }" +
                "        p { margin-bottom: 20px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>Kigali Transport System</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <div class='greeting'>Hello,</div>" +
                "            " + content +
                "            <p>If you didn't request this, you can safely ignore this email.</p>" +
                "            <p>Best regards,<br>The KTRS Team</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            &copy; " + java.time.Year.now().getValue() + " Kigali Transport Reporting System. All rights reserved.<br>" +
                "            Kigali, Rwanda | Excellence in Transit" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public static String generatePasswordResetEmail(String name, String resetLink) {
        String content = "<p>We received a request to reset your password for your KTRS account. Click the button below to set a new one:</p>" +
                "<div style='text-align: center;'>" +
                "    <a href='" + resetLink + "' class='button'>Reset Password</a>" +
                "</div>" +
                "<p style='margin-top: 32px; font-size: 13px; color: #94a3b8;'>Or copy and paste this link into your browser:<br>" +
                "<span style='word-break: break-all; color: " + ACCENT_BLUE + ";'>" + resetLink + "</span></p>";
        return wrapInLayout("Password Reset Request", content);
    }

    public static String generate2FAEmail(String name, String code) {
        String content = "<p>To complete your login, please enter the following verification code. This code will expire in 5 minutes.</p>" +
                "<div class='code'>" + code + "</div>" +
                "<p>Protect your account: Never share this code with anyone. KTRS staff will never ask for your verification code.</p>";
        return wrapInLayout("Verify Your Login", content);
    }
}

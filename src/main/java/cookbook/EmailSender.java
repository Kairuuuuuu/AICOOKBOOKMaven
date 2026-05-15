package cookbook;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    private static final String SENDER_EMAIL = "aicookbooknoreply@gmail.com"; 
    private static final String APP_PASSWORD = "mdpmgszhkczaddhf"; 

    public static String sendOTP(String recipientEmail) {
        
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your AI-CookBook Verification Code");
            
            String htmlMessage = "<html>"
            	    + "<body style='font-family: Arial, sans-serif; color: #333333;'>"
            	    + "<p>Dear " + recipientEmail + ",</p>"
            	    + "<p>Welcome to <b>Dirk's CookBook</b>!</p>"
            	    + "<p>Your verification code is:</p>"
            	    + "<h2 style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #0e4711; margin-bottom: 10px;'>" + otpCode + "</h2>"
            	    + "<p style='font-size: 13px; color: #666666; margin-top: 0px;'>" 
            	    + "Someone tried to use this email account. If this was you, please use the code above to confirm."
            	    + "</p>"
            	    + "<p>Do not share this code with anyone. Get cooking!</p>"
            	    + "</body>"
            	    + "</html>";
            
            message.setContent(htmlMessage, "text/html; charset=utf-8");

            System.out.println("Sending email to " + recipientEmail + "...");
            
            Transport.send(message);
            
            System.out.println("Email sent successfully!");
            return otpCode; 

        } catch (MessagingException e) {
            System.out.println("Failed to send email!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("Enter a real email to test: ");
        String testEmail = scanner.nextLine(); 
        
        System.out.println("Starting Mail Test...");
        String generatedCode = sendOTP(testEmail);
        
        System.out.println("The code we generated and sent was: " + generatedCode);
        scanner.close();
    }
}

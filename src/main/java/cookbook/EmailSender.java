package cookbook;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    // 🌟 1. PUT YOUR INFO HERE 🌟
    private static final String SENDER_EMAIL = "aicookbooknoreply@gmail.com"; 
    private static final String APP_PASSWORD = "mdpmgszhkczaddhf"; 

    // This method generates the code and emails it
    public static String sendOTP(String recipientEmail) {
        
        // Generate a random 6-digit number (e.g. 059284)
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // Setup the Gmail connection rules
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Log into your sender Gmail account
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // Draft the email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your AI-CookBook Verification Code");
            
            // The actual message inside the email
            message.setText("Welcome to Dirk's CookBook, bro!\n\n"
                          + "Your secret verification code is: " + otpCode + "\n\n"
                          + "Do not share this with anyone. Get cooking!");

            System.out.println("Sending email to " + recipientEmail + "...");
            
            // Shoot it across the internet!
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully!");
            return otpCode; // We return the code so your UI can check it later

        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email!");
            e.printStackTrace();
            return null;
        }
    }

    // 🌟 2. THE TEST BENCH 🌟
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
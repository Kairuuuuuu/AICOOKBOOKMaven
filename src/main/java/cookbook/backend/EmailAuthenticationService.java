package cookbook.backend;

public class EmailAuthenticationService {

    public enum AuthStatus {
        SUCCESS, 
        INVALID_EMAIL, 
        CONNECTION_ERROR
    }

    public static class OTPResult {
        public AuthStatus status;
        public String sentCode;

        public OTPResult(AuthStatus status, String sentCode) {
            this.status = status;
            this.sentCode = sentCode;
        }
    }

    /**
     * Validates the email and requests an OTP from the EmailSender.
     */
    public static OTPResult processEmailForOTP(String email) {
        
        // 1. Backend Validation
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return new OTPResult(AuthStatus.INVALID_EMAIL, null);
        }

        // 2. Interact with the external Email Service
        String generatedCode = EmailSender.sendOTP(email.trim());

        // 3. Return the appropriate response state
        if (generatedCode != null) {
            return new OTPResult(AuthStatus.SUCCESS, generatedCode);
        } else {
            return new OTPResult(AuthStatus.CONNECTION_ERROR, null);
        }
    }
}

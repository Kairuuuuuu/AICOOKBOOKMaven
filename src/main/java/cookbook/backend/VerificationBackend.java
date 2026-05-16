package cookbook.backend;

public class VerificationBackend {

    /**
     * Verifies if the entered code matches the expected correct OTP.
     * 
     * @param enteredCode The code typed by the user.
     * @param correctCode The expected verification code.
     * @return true if the codes match, false otherwise.
     */
    public static boolean verifyCode(String enteredCode, String correctCode) {
        if (enteredCode == null || correctCode == null) {
            return false;
        }
        // Trim both to prevent accidental whitespace mismatches
        return enteredCode.trim().equals(correctCode.trim());
    }
}

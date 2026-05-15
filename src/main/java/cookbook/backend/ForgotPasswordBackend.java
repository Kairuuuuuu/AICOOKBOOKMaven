package cookbook.backend;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import cookbook.frontend.ForgotPasswordScreen;
import cookbook.frontend.LoginScreen;

public class ForgotPasswordBackend {

    public static void processPasswordChange(String userEmail, String newPassword, String confirmPassword) {
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty() || newPassword.equals("New Password")) {
            ForgotPasswordScreen.displayError("Please enter a new password!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            ForgotPasswordScreen.displayError("Passwords do not match!");
            return;
        }
        
        // Clear active validation error text objects
        ForgotPasswordScreen.clearError();
        ForgotPasswordScreen.setSubmittingState(true);

        // 2. Multi-threaded cloud processing tasks
        new Thread(() -> {
            String resultMessage = FirebaseManager.changePassword(userEmail, newPassword);

            // Re-sync worker updates back onto the Main Swing Thread Safely
            SwingUtilities.invokeLater(() -> {
                ForgotPasswordScreen.setSubmittingState(false);

                if ("SUCCESS".equalsIgnoreCase(resultMessage)) {
                    ForgotPasswordScreen.closeWindow();
                    JOptionPane.showMessageDialog(null, "Password Successfully Changed!");
                    LoginScreen.main(new String[0]);
                } else {
                    ForgotPasswordScreen.displayError(resultMessage);
                }
            });
        }).start();
    }
}
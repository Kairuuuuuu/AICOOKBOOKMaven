package cookbook.backend;

public class UserProfileBackend {

    // 🌟 PROFILE MEMORY BANK
    public static String firstName = "Guest";
    public static String lastName = "";
    public static String email = "guest@example.com";

    /**
     * Extracts the first letter of the user's first name for the avatar.
     */
    public static String getAvatarInitial() {
        if (firstName == null || firstName.trim().isEmpty()) {
            return "";
        }
        return firstName.substring(0, 1).toUpperCase();
    }

    /**
     * Updates the profile information.
     */
    public static void updateProfile(String newFirstName, String newLastName) {
        firstName = (newFirstName != null) ? newFirstName.trim() : "";
        lastName = (newLastName != null) ? newLastName.trim() : "";
    }

    /**
     * Clears the user session data upon logout.
     */
    public static void performLogout() {
        firstName = "Guest";
        lastName = "";
        email = "guest@example.com";
    }
}

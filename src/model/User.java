package model;
//Represents a system user,each user has a username and personal data file
public class User {

	//Allowed username format(Only letters and numbers are accepted)
    public static final String USERNAME_PATTERN = "[a-zA-Z0-9]+";

    private final String username;

    //Creates a new user
    public User(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty.");
        }
        String trimmed = username.trim();
        if (!trimmed.matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException(
                    "Username can only contain letters and numbers.");
        }
        this.username = trimmed;
    }

    public String getUsername() {
        return username;
    }


    //Returns user data file path
    public String getFilePath() {
        return "data/" + username + ".txt";
    }

    @Override
    //Returns user information as text
    public String toString() {
        return "User{" + username + "}";
    }
}

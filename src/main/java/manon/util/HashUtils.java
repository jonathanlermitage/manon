// SPDX-License-Identifier: MIT

package manon.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    private static final ThreadLocal<MessageDigest> sha256MessageDigest = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize a SHA-256 Message Digest", e);
        }
    });

    /**
     * Hash given password with SHA-256.
     * <p>
     * This is used to bypass BCrypt password max length (72 bytes). We hash passwords before encryption. This is necessary because,
     * starting with Spring Boot 3.4.4, an error is raised if the password is too long. Before, it was simply cropped silently.
     * See <a href="https://security.stackexchange.com/questions/39849/does-bcrypt-have-a-maximum-password-length">security.stackexchange.com</a>.
     */
    public static String hashRawPassword(String password) {
        byte[] hash = sha256MessageDigest.get().digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

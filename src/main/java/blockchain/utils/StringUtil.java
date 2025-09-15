package src.main.java.blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility class for cryptographic hashing operations
 */
public class StringUtil {

    /**
     * Applies SHA-256 hashing to the input string
     *
     * @param input The string to be hashed
     * @return The hexadecimal representation of the SHA-256 hash
     * @throws IllegalArgumentException if input is null
     */
    public static String applySha256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }
}

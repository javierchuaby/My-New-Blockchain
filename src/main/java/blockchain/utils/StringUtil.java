package src.main.java.blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility class for cryptographic hashing operations
 */
public class StringUtil {

    // Thread-local MessageDigest for thread safety and performance
    private static final ThreadLocal<MessageDigest> SHA256_DIGEST =
        ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize SHA-256", e);
            }
        });

    /**
     * Original SHA-256 method (backward compatible)
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

    /**
     * Optimized SHA-256 hashing with thread-local MessageDigest
     */
    public static String applySha256Optimized(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        MessageDigest digest = SHA256_DIGEST.get();
        digest.reset();

        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hash) {
            int val = b & 0xff;
            if (val < 16) {
                hexString.append('0');
            }
            hexString.append(Integer.toHexString(val));
        }
        return hexString.toString();
    }
}

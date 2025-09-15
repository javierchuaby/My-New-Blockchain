package src.main.java.blockchain.core;

import src.main.java.blockchain.utils.StringUtil;

import java.time.Instant;

/**
 * Represents a single block in the blockchain.
 * Each block contains data, references the previous block, and includes proof-of-work.
 */
public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    /**
     * Creates a new block with the specified data and previous block hash
     *
     * @param data         The data to store in this block
     * @param previousHash The hash of the previous block in the chain
     * @throws IllegalArgumentException if data or previousHash is null or empty
     */
    public Block(String data, String previousHash) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("src.main.java.blockchain.core.Block data cannot be null or empty");
        }
        if (previousHash == null) {
            throw new IllegalArgumentException("Previous hash cannot be null");
        }

        this.data = data.trim();
        this.previousHash = previousHash;
        this.timeStamp = Instant.now().toEpochMilli();
        this.nonce = 0;
        this.hash = calculateHash();
    }

    /**
     * Calculates the hash for this block based on its contents
     *
     * @return The SHA-256 hash of the block's contents
     */
    public String calculateHash() {
        String input = previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                data;
        return StringUtil.applySha256(input);
    }

    /**
     * Mines the block by finding a hash that starts with the required number of zeros.
     * This implements the proof-of-work consensus mechanism.
     *
     * @param difficulty The number of leading zeros required in the hash
     * @throws IllegalArgumentException if difficulty is negative or too high
     */
    public void mineBlock(int difficulty) {
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty cannot be negative");
        }
        if (difficulty > 10) {
            throw new IllegalArgumentException("Difficulty too high (max 10)");
        }

        String target = "0".repeat(difficulty);
        long startTime = System.currentTimeMillis();

        System.out.printf("Mining block with difficulty %d...%n", difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();

            // Show progress every 100,000 attempts for high difficulty
            if (difficulty >= 5 && nonce % 100000 == 0) {
                System.out.printf("Attempts: %,d%n", nonce);
            }
        }

        long endTime = System.currentTimeMillis();
        long miningTime = endTime - startTime;

        System.out.printf("src.main.java.blockchain.core.Block mined successfully!%n");
        System.out.printf("Hash: %s%n", hash);
        System.out.printf("Nonce: %,d%n", nonce);
        System.out.printf("Mining time: %,d ms%n%n", miningTime);
    }

    // Getters
    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    /**
     * Returns a string representation of the block for debugging
     */
    @Override
    public String toString() {
        return String.format("src.main.java.blockchain.core.Block{hash='%s', previousHash='%s', data='%s', timeStamp=%d, nonce=%d}",
                hash, previousHash, data, timeStamp, nonce);
    }
}

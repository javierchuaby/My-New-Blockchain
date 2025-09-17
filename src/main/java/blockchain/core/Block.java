package src.main.java.blockchain.core;

import src.main.java.blockchain.utils.StringUtil;
import src.main.java.blockchain.mining.MiningPool;

import java.time.Instant;

/**
 * Represents a single block in the blockchain.
 * Each block contains data, references the previous block, and includes proof-of-work.
 */
public class Block {
    private volatile String hash;        // Made volatile for thread safety
    private final String previousHash;
    private final String data;
    private volatile long timeStamp;     // Made volatile for database reconstruction
    private volatile int nonce;          // Made volatile for concurrent mining

    /**
     * Creates a new block with the specified data and previous block hash
     *
     * @param data The data to store in this block
     * @param previousHash The hash of the previous block in the chain
     * @throws IllegalArgumentException if data or previousHash is null or empty
     */
    public Block(String data, String previousHash) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Block data cannot be null or empty");
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
     * Static factory method for reconstructing blocks from database
     */
    public static Block fromDatabase(String hash, String previousHash, String data,
                                     long timestamp, int nonce) {
        Block block = new Block(data, previousHash);
        block.timeStamp = timestamp;  // Override constructor timestamp
        block.nonce = nonce;
        block.hash = hash;
        return block;
    }

    /**
     * Calculates the hash for this block based on its contents
     */
    public String calculateHash() {
        String input = previousHash +
            Long.toString(timeStamp) +
            Integer.toString(nonce) +
            data;
        return StringUtil.applySha256(input);
    }

    /**
     * Calculates hash with a specific nonce (used in concurrent mining)
     */
    public String calculateHashWithNonce(int testNonce) {
        String input = previousHash +
            Long.toString(timeStamp) +
            Integer.toString(testNonce) +
            data;
        return StringUtil.applySha256(input);
    }

    /**
     * Original mining method (backward compatible)
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
            if (difficulty >= 5 && nonce % 100000 == 0) {
                System.out.printf("Attempts: %,d%n", nonce);
            }
        }

        long endTime = System.currentTimeMillis();
        long miningTime = endTime - startTime;
        System.out.printf("Block mined successfully!%n");
        System.out.printf("Hash: %s%n", hash);
        System.out.printf("Nonce: %,d%n", nonce);
        System.out.printf("Mining time: %,d ms%n%n", miningTime);
    }

    /**
     * Concurrent mining method for performance
     */
    public void mineBlockConcurrent(int difficulty, int threadCount) {
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty cannot be negative");
        }
        if (difficulty > 10) {
            throw new IllegalArgumentException("Difficulty too high (max 10)");
        }

        MiningPool miningPool = new MiningPool(threadCount);
        long startTime = System.currentTimeMillis();

        System.out.printf("Mining block with difficulty %d using %d threads...%n", difficulty, threadCount);

        int winningNonce = miningPool.mineBlockConcurrently(this, difficulty);

        this.nonce = winningNonce;
        this.hash = calculateHash();

        long endTime = System.currentTimeMillis();
        System.out.printf("Block mined successfully in %d ms!%n", (endTime - startTime));
        System.out.printf("Hash: %s%nNonce: %d%n%n", hash, nonce);

        miningPool.shutdown();
    }

    // Getters
    public String getHash() { return hash; }
    public String getPreviousHash() { return previousHash; }
    public String getData() { return data; }
    public long getTimeStamp() { return timeStamp; }
    public int getNonce() { return nonce; }

    // Setters for thread-safe operations
    public void setHash(String hash) { this.hash = hash; }
    public void setNonce(int nonce) { this.nonce = nonce; }

    @Override
    public String toString() {
        return String.format("Block{hash='%s', previousHash='%s', data='%s', timeStamp=%d, nonce=%d}",
            hash, previousHash, data, timeStamp, nonce);
    }
}

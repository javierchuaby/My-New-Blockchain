package src.main.java.blockchain.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

/**
 * A simple blockchain implementation with proof-of-work consensus mechanism.
 * This class manages a chain of blocks and provides validation functionality.
 */
public class Blockchain {
    private final List<Block> chain;
    private final int difficulty;

    // Configuration constants
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 10;
    public static final int DEFAULT_DIFFICULTY = 4;

    /**
     * Creates a new blockchain with the specified mining difficulty
     *
     * @param difficulty The mining difficulty (number of leading zeros required)
     * @throws IllegalArgumentException if difficulty is outside valid range
     */
    public Blockchain(int difficulty) {
        if (difficulty < MIN_DIFFICULTY || difficulty > MAX_DIFFICULTY) {
            throw new IllegalArgumentException(
                    String.format("Difficulty must be between %d and %d", MIN_DIFFICULTY, MAX_DIFFICULTY));
        }

        this.difficulty = difficulty;
        this.chain = new ArrayList<>();

        // Create and mine the genesis block
        createGenesisBlock();
    }

    /**
     * Creates a blockchain with default difficulty
     */
    public Blockchain() {
        this(DEFAULT_DIFFICULTY);
    }

    /**
     * Creates the first block in the chain (genesis block)
     */
    private void createGenesisBlock() {
        System.out.println("Creating genesis block...");
        Block genesis = new Block("Genesis src.main.java.blockchain.core.Block - The beginning of the chain", "0");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
        System.out.println("Genesis block created successfully!\n");
    }

    /**
     * Adds a new block to the blockchain with the specified data
     *
     * @param data The data to store in the new block
     * @throws IllegalArgumentException if data is null or empty
     */
    public void addBlock(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("src.main.java.blockchain.core.Block data cannot be null or empty");
        }

        String previousHash = getLatestBlock().getHash();
        Block newBlock = new Block(data, previousHash);

        System.out.printf("Adding new block (src.main.java.blockchain.core.Block #%d)...%n", chain.size() + 1);
        newBlock.mineBlock(difficulty);

        chain.add(newBlock);
        System.out.printf("src.main.java.blockchain.core.Block #%d added successfully!%n%n", chain.size());
    }

    /**
     * Gets the most recently added block in the chain
     *
     * @return The latest block
     */
    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("src.main.java.blockchain.core.Blockchain is empty");
        }
        return chain.get(chain.size() - 1);
    }

    /**
     * Validates the integrity of the entire blockchain
     *
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isChainValid() {
        String hashTarget = "0".repeat(difficulty);

        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Validate current block's hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.err.printf("Invalid hash detected at block %d%n", i);
                return false;
            }

            // Validate link to previous block
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.err.printf("Invalid previous hash link at block %d%n", i);
                return false;
            }

            // Validate proof-of-work
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.err.printf("src.main.java.blockchain.core.Block %d was not properly mined (invalid proof-of-work)%n", i);
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the blockchain as a formatted JSON string
     *
     * @return JSON representation of the blockchain
     */
    public String toJson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(chain);
    }

    /**
     * Gets the number of blocks in the chain
     *
     * @return The size of the blockchain
     */
    public int size() {
        return chain.size();
    }

    /**
     * Gets the difficulty setting for this blockchain
     *
     * @return The mining difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Gets a block at the specified index
     *
     * @param index The index of the block to retrieve
     * @return The block at the specified index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Invalid block index: %d (valid range: 0-%d)", index, chain.size() - 1));
        }
        return chain.get(index);
    }

    /**
     * Displays blockchain statistics
     */
    public void printStats() {
        System.out.println("=== BLOCKCHAIN STATISTICS ===");
        System.out.printf("Total blocks: %d%n", size());
        System.out.printf("Mining difficulty: %d%n", difficulty);
        System.out.printf("Latest block hash: %s%n", getLatestBlock().getHash());
        System.out.printf("Genesis block hash: %s%n", chain.get(0).getHash());
        System.out.println("===============================\n");
    }
}

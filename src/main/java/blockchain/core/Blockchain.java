package src.main.java.blockchain.core;

import src.main.java.blockchain.persistence.BlockchainDAO;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.GsonBuilder;

/**
 * Enhanced blockchain implementation with proof-of-work consensus mechanism.
 */
public class Blockchain {
    private final List<Block> chain;
    private final int difficulty;
    private final BlockchainDAO dao;
    private final int miningThreads;

    // Configuration constants
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 10;
    public static final int DEFAULT_DIFFICULTY = 4;
    public static final int DEFAULT_THREADS = 1;

    /**
     * Enhanced constructor with database and threading support
     */
    public Blockchain(int difficulty, String dbUrl, int miningThreads) {
        if (difficulty < MIN_DIFFICULTY || difficulty > MAX_DIFFICULTY) {
            throw new IllegalArgumentException(
                String.format("Difficulty must be between %d and %d", MIN_DIFFICULTY, MAX_DIFFICULTY));
        }

        this.difficulty = difficulty;
        this.miningThreads = Math.max(1, miningThreads);
        this.dao = (dbUrl != null) ? new BlockchainDAO(dbUrl) : null;
        this.chain = new ArrayList<>();

        loadOrCreateBlockchain();
    }

    /**
     * Original constructor (backward compatible)
     */
    public Blockchain(int difficulty) {
        this(difficulty, null, DEFAULT_THREADS);
    }

    /**
     * Default constructor (backward compatible)
     */
    public Blockchain() {
        this(DEFAULT_DIFFICULTY, null, DEFAULT_THREADS);
    }

    private void loadOrCreateBlockchain() {
        if (isDatabaseEnabled()) {
            List<Block> existingBlocks = dao.loadBlockchain();
            if (existingBlocks.isEmpty()) {
                createGenesisBlock();
            } else {
                chain.addAll(existingBlocks);
                System.out.printf("Loaded %d blocks from database%n", existingBlocks.size());
            }
        } else {
            createGenesisBlock();
        }
    }

    private void createGenesisBlock() {
        System.out.println("Creating genesis block...");
        Block genesis = new Block("Genesis Block - The beginning of the chain", "0");

        if (isConcurrentMiningEnabled()) {
            genesis.mineBlockConcurrent(difficulty, miningThreads);
        } else {
            genesis.mineBlock(difficulty);
        }

        chain.add(genesis);

        if (isDatabaseEnabled()) {
            dao.saveBlock(genesis, difficulty);
        }

        System.out.println("Genesis block created successfully!\n");
    }

    public void addBlock(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Block data cannot be null or empty");
        }

        String previousHash = getLatestBlock().getHash();
        Block newBlock = new Block(data, previousHash);

        System.out.printf("Adding new block (Block #%d)...%n", chain.size() + 1);

        if (isConcurrentMiningEnabled()) {
            newBlock.mineBlockConcurrent(difficulty, miningThreads);
        } else {
            newBlock.mineBlock(difficulty);
        }

        chain.add(newBlock);

        if (isDatabaseEnabled()) {
            dao.saveBlock(newBlock, difficulty);
        }

        System.out.printf("Block #%d added successfully!%n%n", chain.size());
    }

    // Keep all your existing methods (getLatestBlock, isChainValid, etc.)
    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("Blockchain is empty");
        }
        return chain.get(chain.size() - 1);
    }

    public boolean isChainValid() {
        String hashTarget = "0".repeat(difficulty);

        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.err.printf("Invalid hash detected at block %d%n", i);
                return false;
            }

            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.err.printf("Invalid previous hash link at block %d%n", i);
                return false;
            }

            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.err.printf("Block %d was not properly mined (invalid proof-of-work)%n", i);
                return false;
            }
        }
        return true;
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(chain);
    }

    public int size() { return chain.size(); }
    public int getDifficulty() { return difficulty; }
    public int getMiningThreads() { return miningThreads; }

    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid block index: %d (valid range: 0-%d)", index, chain.size() - 1));
        }
        return chain.get(index);
    }

    public void printStats() {
        System.out.println("=== ENHANCED BLOCKCHAIN STATISTICS ===");
        System.out.printf("Total blocks: %d%n", size());
        System.out.printf("Mining difficulty: %d%n", difficulty);
        System.out.printf("Mining threads: %d%n", miningThreads);
        System.out.printf("Database enabled: %s%n", isDatabaseEnabled() ? "Yes" : "No");
        System.out.printf("Latest block hash: %s%n", getLatestBlock().getHash());
        System.out.printf("Genesis block hash: %s%n", chain.get(0).getHash());

        if (isDatabaseEnabled()) {
            System.out.printf("Database stats: %s%n", dao.getBlockchainStats());
        }

        System.out.println("=========================================\n");
    }

    private boolean isDatabaseEnabled() { return dao != null; }
    private boolean isConcurrentMiningEnabled() { return miningThreads > 1; }

    public void close() {
        if (isDatabaseEnabled()) {
            dao.close();
        }
    }
}

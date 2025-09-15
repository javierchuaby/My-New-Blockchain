package src.main.java.blockchain.demo;

import src.main.java.blockchain.core.Block;
import src.main.java.blockchain.core.Blockchain;

/**
 * Demonstration program for the blockchain implementation.
 * This class shows how to create a blockchain, add blocks, and validate the chain.
 */
public class BlockchainDemo {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    SIMPLE BLOCKCHAIN DEMONSTRATION    ");
        System.out.println("========================================\n");

        try {
            // Create a new blockchain with moderate difficulty for demo
            Blockchain myBlockchain = new Blockchain(6);

            // Add some sample blocks with transaction-like data
            System.out.println("Adding sample transactions to the blockchain...\n");

            myBlockchain.addBlock("Alice sends 50 coins to Bob");
            myBlockchain.addBlock("Bob sends 25 coins to Charlie");
            myBlockchain.addBlock("Charlie sends 10 coins to David");
            myBlockchain.addBlock("David sends 5 coins back to Alice");

            // Display blockchain statistics
            myBlockchain.printStats();

            // Validate the entire blockchain
            System.out.println("=== BLOCKCHAIN VALIDATION ===");
            boolean isValid = myBlockchain.isChainValid();
            System.out.printf("src.main.java.blockchain.core.Blockchain integrity check: %s%n",
                    isValid ? "✓ VALID" : "✗ INVALID");

            if (isValid) {
                System.out.println("All blocks are properly linked and mined!");
            }
            System.out.println("==============================\n");

            // Display individual block information
            System.out.println("=== BLOCK DETAILS ===");
            for (int i = 0; i < myBlockchain.size(); i++) {
                Block block = myBlockchain.getBlock(i);
                System.out.printf("src.main.java.blockchain.core.Block #%d:%n", i);
                System.out.printf("  Data: %s%n", block.getData());
                System.out.printf("  Hash: %s%n", block.getHash());
                System.out.printf("  Previous Hash: %s%n", block.getPreviousHash());
                System.out.printf("  Nonce: %,d%n", block.getNonce());
                System.out.printf("  Timestamp: %d%n%n", block.getTimeStamp());
            }

            // Display the complete blockchain as JSON
            System.out.println("=== COMPLETE BLOCKCHAIN (JSON) ===");
            System.out.println(myBlockchain.toJson());

        } catch (Exception e) {
            System.err.println("Error during blockchain demonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

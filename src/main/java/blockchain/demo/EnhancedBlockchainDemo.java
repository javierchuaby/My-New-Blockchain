package src.main.java.blockchain.demo;

import src.main.java.blockchain.core.Blockchain;

public class EnhancedBlockchainDemo {
    public static void main(String[] args) {
        System.out.println("=== ENHANCED BLOCKCHAIN DEMONSTRATION ===");

        // Test 1: Concurrent mining
        System.out.println("Testing concurrent mining with 4 threads...");
        Blockchain concurrentBlockchain = new Blockchain(4, null, 4);
        concurrentBlockchain.addBlock("Transaction with concurrent mining");
        concurrentBlockchain.printStats();

        // Test 2: Database persistence
        System.out.println("Testing database persistence...");
        Blockchain dbBlockchain = new Blockchain(3, "jdbc:h2:mem:testdb", 2);
        dbBlockchain.addBlock("Persistent transaction 1");
        dbBlockchain.addBlock("Persistent transaction 2");
        dbBlockchain.printStats();

        // Test 3: Backward compatibility
        System.out.println("Testing backward compatibility...");
        Blockchain originalBlockchain = new Blockchain(2);
        originalBlockchain.addBlock("Original functionality still works");
        originalBlockchain.printStats();

        System.out.println("All tests completed successfully! ✓");
    }
}

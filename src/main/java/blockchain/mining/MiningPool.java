package src.main.java.blockchain.mining;

import src.main.java.blockchain.core.Block;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pool for concurrent blockchain mining operations
 */
public class MiningPool {
    private final int threadCount;
    private final ExecutorService executorService;
    private final AtomicBoolean solutionFound = new AtomicBoolean(false);
    private final AtomicInteger winningNonce = new AtomicInteger(0);

    public MiningPool(int threadCount) {
        this.threadCount = Math.max(1, threadCount);
        this.executorService = Executors.newFixedThreadPool(this.threadCount);
    }

    public int mineBlockConcurrently(Block block, int difficulty) {
        solutionFound.set(false);
        winningNonce.set(0);

        String target = "0".repeat(difficulty);
        CountDownLatch latch = new CountDownLatch(1);

        int rangePerThread = Integer.MAX_VALUE / threadCount;

        for (int i = 0; i < threadCount; i++) {
            final int startNonce = i * rangePerThread;
            final int endNonce = (i == threadCount - 1) ? Integer.MAX_VALUE : (i + 1) * rangePerThread;

            executorService.submit(new MiningTask(block, difficulty, startNonce, endNonce, target, latch));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Mining was interrupted", e);
        }

        return winningNonce.get();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private class MiningTask implements Runnable {
        private final Block block;
        private final int difficulty;
        private final int startNonce;
        private final int endNonce;
        private final String target;
        private final CountDownLatch latch;

        public MiningTask(Block block, int difficulty, int startNonce, int endNonce,
                          String target, CountDownLatch latch) {
            this.block = block;
            this.difficulty = difficulty;
            this.startNonce = startNonce;
            this.endNonce = endNonce;
            this.target = target;
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int nonce = startNonce; nonce < endNonce && !solutionFound.get(); nonce++) {
                String hash = block.calculateHashWithNonce(nonce);
                if (hash.substring(0, difficulty).equals(target)) {
                    if (solutionFound.compareAndSet(false, true)) {
                        winningNonce.set(nonce);
                        latch.countDown();
                        break;
                    }
                }
            }
        }
    }
}

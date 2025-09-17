package src.main.java.blockchain.persistence;

import src.main.java.blockchain.core.Block;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Data Access Object for blockchain persistence operations
 */
public class BlockchainDAO {
    private final HikariDataSource dataSource;

    public BlockchainDAO(String dbUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);
        initializeTables();
    }

    private void initializeTables() {
        String createBlocksTable = """
            CREATE TABLE IF NOT EXISTS blocks (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                hash VARCHAR(64) UNIQUE NOT NULL,
                previous_hash VARCHAR(64) NOT NULL,
                data TEXT NOT NULL,
                timestamp BIGINT NOT NULL,
                nonce INTEGER NOT NULL,
                difficulty INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createBlocksTable);

            // Create indexes for performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_hash ON blocks(hash)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_previous_hash ON blocks(previous_hash)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_timestamp ON blocks(timestamp)");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public void saveBlock(Block block, int difficulty) {
        String sql = "INSERT INTO blocks (hash, previous_hash, data, timestamp, nonce, difficulty) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, block.getHash());
            stmt.setString(2, block.getPreviousHash());
            stmt.setString(3, block.getData());
            stmt.setLong(4, block.getTimeStamp());
            stmt.setInt(5, block.getNonce());
            stmt.setInt(6, difficulty);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save block: " + block.getHash(), e);
        }
    }

    public List<Block> loadBlockchain() {
        String sql = "SELECT hash, previous_hash, data, timestamp, nonce FROM blocks ORDER BY id";
        List<Block> blocks = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Block block = Block.fromDatabase(
                    rs.getString("hash"),
                    rs.getString("previous_hash"),
                    rs.getString("data"),
                    rs.getLong("timestamp"),
                    rs.getInt("nonce")
                );
                blocks.add(block);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load blockchain", e);
        }

        return blocks;
    }

    public String getBlockchainStats() {
        String sql = """
            SELECT 
                COUNT(*) as block_count,
                MIN(timestamp) as earliest_block,
                MAX(timestamp) as latest_block,
                AVG(nonce) as avg_nonce
            FROM blocks
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return String.format(
                    "Database Stats: %d blocks, Earliest: %d, Latest: %d, Avg Nonce: %.0f",
                    rs.getInt("block_count"),
                    rs.getLong("earliest_block"),
                    rs.getLong("latest_block"),
                    rs.getDouble("avg_nonce")
                );
            }

        } catch (SQLException e) {
            System.err.println("Failed to get statistics: " + e.getMessage());
        }

        return "Statistics unavailable";
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

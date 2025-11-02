package enrollmentsystem;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.sql.*;
import java.util.*;

public class EmbeddedDatabaseManager {
    private static Process serverProcess;
    private static final int EMBEDDED_PORT = 3307;
    private static final String DB_NAME = "enrollment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    /**
     * Start the embedded database - COMPLETE MANUAL CONTROL
     */
    public static void startEmbeddedDatabase() {
        try {
            String dataDir = getDataDirectory();
            String baseDir = System.getProperty("java.io.tmpdir") + File.separator + "MariaDB4j_BASE";
            
            System.out.println("Starting embedded MariaDB database...");
            System.out.println("Base directory: " + baseDir);
            System.out.println("Data directory: " + dataDir);
            
            // CRITICAL: Kill any orphaned mysqld processes first
            killOrphanedMariaDBProcesses();
            
            // CRITICAL: Force complete cleanup if corrupted
            if (hasCorruptedInnoDBFiles(dataDir)) {
                System.out.println("⚠ Detected corrupted or locked InnoDB files");
                System.out.println("⚠ Forcing complete cleanup...");
                forceCleanDataDirectory(dataDir);
                System.out.println("✓ Data directory cleaned");
            }
            
            // Step 1: Unpack MariaDB binaries if needed
            File binDir = new File(baseDir, "bin");
            if (!binDir.exists()) {
                System.out.println("Unpacking MariaDB binaries...");
                unpackMariaDBBinaries(baseDir);
            } else {
                System.out.println("MariaDB binaries already unpacked");
            }
            
            // Step 2: Install database if needed (first run)
            File mysqlDir = new File(dataDir, "mysql");
            boolean isFirstInstall = !mysqlDir.exists();
            
            if (isFirstInstall) {
                System.out.println("First run - installing database...");
                installDatabase(baseDir, dataDir);
                // Only fix permissions on first install
                forceFixPermissions(dataDir);
            } else {
                System.out.println("Database already installed, skipping permission fix");
            }
            
            // Step 3: Start MariaDB server process manually
            System.out.println("Starting MariaDB server...");
            startMariaDBServer(baseDir, dataDir);
            
            // Step 4: Wait for server to be ready and create database
            System.out.println("Waiting for MariaDB to accept connections...");
            if (!waitForServerReady()) {
                throw new RuntimeException("MariaDB server failed to start within timeout period");
            }
            
            // Step 5: Create database and import schema
            initializeDatabaseSchema();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stopEmbeddedDatabase();
            }));
            
            System.out.println("✓ Embedded database is ready!");
            
        } catch (Exception e) {
            System.err.println("Failed to start embedded database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * CRITICAL: Kill any orphaned mysqld.exe processes
     */
    private static void killOrphanedMariaDBProcesses() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                System.out.println("Checking for orphaned MariaDB processes...");
                
                // Kill any mysqld.exe on our port
                ProcessBuilder pb = new ProcessBuilder(
                    "cmd.exe", "/c", 
                    "for /f \"tokens=5\" %a in ('netstat -aon ^| findstr :3307') do taskkill /F /PID %a"
                );
                Process p = pb.start();
                p.waitFor();
                
                // Also kill by process name as backup
                ProcessBuilder pb2 = new ProcessBuilder(
                    "taskkill", "/F", "/IM", "mysqld.exe", "/T"
                );
                pb2.redirectErrorStream(true);
                Process p2 = pb2.start();
                p2.waitFor();
                
                Thread.sleep(1000); // Let processes fully terminate
                System.out.println("✓ Checked for orphaned processes");
            }
        } catch (Exception e) {
            // Non-critical, continue
            System.out.println("Note: Could not check for orphaned processes: " + e.getMessage());
        }
    }
    
    /**
     * Check for corrupted or locked InnoDB files
     */
    private static boolean hasCorruptedInnoDBFiles(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            return false;
        }
        
        // Check for InnoDB system files
        String[] criticalFiles = {
            "ibdata1", 
            "ib_logfile0", 
            "ib_logfile1",
            "aria_log_control",
            "ib_buffer_pool"
        };
        
        for (String filename : criticalFiles) {
            File file = new File(dataDir, filename);
            if (file.exists()) {
                // Try to detect if file is locked or corrupted
                if (!file.canWrite() || !file.canRead()) {
                    System.out.println("⚠ Found inaccessible file: " + filename);
                    return true;
                }
                
                // Check if file is zero bytes (corrupted)
                if (file.length() == 0 && !filename.equals("ib_buffer_pool")) {
                    System.out.println("⚠ Found zero-byte file: " + filename);
                    return true;
                }
            }
        }
        
        // Check for .lock files
        File[] lockFiles = dir.listFiles((d, name) -> 
            name.endsWith(".lock") || name.endsWith(".lck")
        );
        if (lockFiles != null && lockFiles.length > 0) {
            System.out.println("⚠ Found lock files from previous run");
            return true;
        }
        
        return false;
    }
    
    /**
     * AGGRESSIVE: Force clean data directory with multiple strategies
     */
    private static void forceCleanDataDirectory(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }
        
        System.out.println("Attempting aggressive cleanup of: " + dataDir);
        
        try {
            // Strategy 1: Use Java NIO with POSIX permissions
            Path dirPath = Paths.get(dataDir);
            
            // Walk the file tree and delete everything
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        // Remove read-only attribute first
                        Files.setAttribute(file, "dos:readonly", false);
                        // Force delete
                        Files.delete(file);
                    } catch (IOException e) {
                        // If Java NIO fails, try File API
                        file.toFile().delete();
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    try {
                        Files.delete(dir);
                    } catch (IOException e) {
                        dir.toFile().delete();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
        } catch (Exception e) {
            System.err.println("NIO cleanup failed, trying fallback: " + e.getMessage());
        }
        
        // Strategy 2: Fallback to recursive delete with forced attributes
        deleteDirectoryWithForce(dir);
        
        // Strategy 3: Use Windows-specific commands as last resort
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "cmd.exe", "/c", 
                    "rd /s /q \"" + dataDir + "\""
                );
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                // Ignore, best effort
            }
        }
        
        // Recreate empty directory
        dir.mkdirs();
        System.out.println("✓ Data directory forcefully cleaned");
    }
    
    /**
     * Recursive delete with forced attribute removal
     */
    private static void deleteDirectoryWithForce(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryWithForce(file);
                    } else {
                        // Remove all restrictive attributes
                        file.setWritable(true, false);
                        file.setReadable(true, false);
                        
                        // Try to delete
                        if (!file.delete()) {
                            // Mark for deletion on exit as fallback
                            file.deleteOnExit();
                            System.out.println("⚠ Marking for deletion on exit: " + file.getName());
                        }
                    }
                }
            }
            
            // Try to delete directory
            directory.setWritable(true, false);
            if (!directory.delete()) {
                directory.deleteOnExit();
            }
        }
    }
    
    /**
     * AGGRESSIVE: Force fix permissions with timeout protection
     */
    private static void forceFixPermissions(String dataDir) {
        System.out.println("Applying aggressive permissions fix...");
        
        // Use a thread with timeout to prevent hanging
        Thread permThread = new Thread(() -> {
            try {
                // Strategy 1: Windows-specific command FIRST (fastest and most reliable)
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    System.out.println("Using Windows icacls command...");
                    ProcessBuilder pb = new ProcessBuilder(
                        "icacls", dataDir, "/grant", "Everyone:(F)", "/T", "/C", "/Q"
                    );
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    
                    // Wait max 10 seconds for icacls
                    boolean completed = p.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
                    if (completed && p.exitValue() == 0) {
                        System.out.println("✓ Windows ACL permissions applied");
                        return;
                    } else {
                        System.out.println("⚠ icacls timeout or failed, using fallback");
                    }
                }
                
                // Strategy 2: Simple recursive fix (non-blocking)
                System.out.println("Applying simple permissions fix...");
                fixPermissionsSimple(new File(dataDir));
                System.out.println("✓ Basic permissions applied");
                
            } catch (Exception e) {
                System.err.println("Permissions fix error: " + e.getMessage());
            }
        }, "PermissionFixer");
        
        permThread.setDaemon(true);
        permThread.start();
        
        try {
            // Wait max 15 seconds for permission fix
            permThread.join(15000);
            
            if (permThread.isAlive()) {
                System.err.println("⚠ Permission fix timeout, continuing anyway...");
                permThread.interrupt();
            }
        } catch (InterruptedException e) {
            System.err.println("Permission fix interrupted");
        }
    }
    
    /**
     * Simple, non-blocking permissions fix
     */
    private static void fixPermissionsSimple(File directory) {
        if (!directory.exists()) {
            return;
        }
        
        try {
            // Fix directory itself
            directory.setWritable(true, false);
            directory.setReadable(true, false);
            directory.setExecutable(true, false);
            
            // Fix all direct children only (don't recurse deep)
            File[] files = directory.listFiles();
            if (files != null) {
                int count = 0;
                for (File file : files) {
                    try {
                        file.setWritable(true, false);
                        file.setReadable(true, false);
                        if (file.isDirectory()) {
                            file.setExecutable(true, false);
                            // Only go one level deep
                            File[] subFiles = file.listFiles();
                            if (subFiles != null) {
                                for (File sub : subFiles) {
                                    sub.setWritable(true, false);
                                    sub.setReadable(true, false);
                                }
                            }
                        }
                        count++;
                        
                        // Limit processing to prevent hanging
                        if (count > 100) {
                            System.out.println("⚠ Limiting permission fix to 100 files");
                            break;
                        }
                    } catch (Exception e) {
                        // Skip problematic files
                    }
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
    }
    
    /**
     * Unpack MariaDB binaries using MariaDB4j
     */
    private static void unpackMariaDBBinaries(String baseDir) {
        try {
            DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
            configBuilder.setBaseDir(baseDir);
            configBuilder.setPort(EMBEDDED_PORT);
            
            // This will fail with tmpdir error, but it will extract the files first
            try {
                DB tempDB = DB.newEmbeddedDB(configBuilder.build());
            } catch (Exception e) {
                // Expected to fail, but files should be extracted
                System.out.println("Binaries extracted (tmpdir error expected)");
            }
            
            // Verify binaries were extracted
            File mysqldExe = new File(baseDir, "bin" + File.separator + "mysqld.exe");
            if (mysqldExe.exists()) {
                System.out.println("MariaDB binaries successfully extracted to: " + baseDir);
            } else {
                throw new RuntimeException("Failed to extract MariaDB binaries");
            }
            
        } catch (Exception e) {
            System.err.println("Error unpacking binaries: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Install database manually without tmpdir
     */
    private static void installDatabase(String baseDir, String dataDir) {
        try {
            File installExe = new File(baseDir, "bin" + File.separator + "mysql_install_db.exe");
            
            if (!installExe.exists()) {
                throw new RuntimeException("mysql_install_db.exe not found at: " + installExe.getAbsolutePath());
            }
            
            // Create data directory
            new File(dataDir).mkdirs();
            
            System.out.println("Running mysql_install_db...");
            
            ProcessBuilder pb = new ProcessBuilder(
                installExe.getAbsolutePath(),
                "--datadir=" + dataDir,
                "--default-user"
            );
            pb.directory(new File(baseDir));
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Capture output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[mysql_install_db] " + line);
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Database installed successfully!");
            } else {
                throw new RuntimeException("mysql_install_db failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            System.err.println("Database installation failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Start MariaDB server with MINIMAL InnoDB requirements
     */
    private static void startMariaDBServer(String baseDir, String dataDir) {
        try {
            File mysqldExe = new File(baseDir, "bin" + File.separator + "mysqld.exe");
            
            if (!mysqldExe.exists()) {
                throw new RuntimeException("mysqld.exe not found");
            }
            
            System.out.println("Starting MariaDB with minimal InnoDB config...");
            
            ProcessBuilder pb = new ProcessBuilder(
                mysqldExe.getAbsolutePath(),
                "--no-defaults",
                "--bind-address=127.0.0.1",
                "--port=" + EMBEDDED_PORT,
                "--datadir=" + dataDir,
                "--basedir=" + baseDir,
                // CRITICAL: Minimal InnoDB configuration
                "--innodb-flush-method=normal",
                "--innodb-use-native-aio=0",
                "--innodb-flush-log-at-trx-commit=2",
                "--innodb-file-per-table=1",
                "--innodb-buffer-pool-size=32M",        // REDUCED
                "--innodb-log-file-size=8M",            // REDUCED
                "--innodb-log-buffer-size=4M",          // REDUCED
                "--skip-innodb-doublewrite",
                "--innodb-fast-shutdown=0",             // ADDED: Clean shutdown
                "--innodb-force-recovery=0",            // ADDED: No recovery mode
                // General
                "--max-connections=25",                  // REDUCED
                "--table-open-cache=50",                // REDUCED
                "--skip-name-resolve",                   // ADDED: Performance
                "--console"
            );
            pb.directory(new File(baseDir));
            pb.redirectErrorStream(true);
            
            serverProcess = pb.start();
            
            // Monitor output
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(serverProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("ready for connections")) {
                            System.out.println("[MariaDB] ✓ Server ready!");
                        } else if (line.contains("ERROR") && !line.contains("WSREP")) {
                            System.err.println("[MariaDB] " + line);
                        } else if (line.contains("InnoDB") && line.contains("started")) {
                            System.out.println("[MariaDB] ✓ InnoDB started");
                        }
                    }
                } catch (IOException e) {
                    // Server stopped
                }
            }, "MariaDB-Monitor").start();
            
            System.out.println("MariaDB process started (PID: " + serverProcess.pid() + ")");
            
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Wait for MariaDB server to be ready to accept connections
     */
    private static boolean waitForServerReady() {
        String url = "jdbc:mysql://localhost:" + EMBEDDED_PORT + 
                     "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=1000";
        
        int maxAttempts = 30; // 30 seconds total
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Thread.sleep(1000); // Wait 1 second between attempts
                try (Connection testConn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD)) {
                    System.out.println("✓ MariaDB ready to accept connections (attempt " + (i + 1) + ")");
                    return true;
                }
            } catch (SQLException e) {
                // Server not ready yet, continue waiting
                if (i % 5 == 0 && i > 0) {
                    System.out.print(".");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        System.err.println("\n✗ MariaDB server did not become ready within " + maxAttempts + " seconds");
        return false;
    }
    
    /**
     * Initialize database schema
     */
    private static void initializeDatabaseSchema() {
        System.out.println("Initializing schema...");
        
        try {
            Connection initialConn = getEmbeddedConnectionWithoutDB();
            
            try (Statement stmt = initialConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("Database '" + DB_NAME + "' created");
            }
            initialConn.close();
            
            Connection conn = getEmbeddedConnection();
            
            if (isFirstRun()) {
                importDatabaseSchema(conn);
            } else {
                System.out.println("Schema already initialized");
            }
            
            conn.close();
            
        } catch (Exception e) {
            System.err.println("Schema init error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Import schema from SQL file
     */
    private static void importDatabaseSchema(Connection conn) {
        System.out.println("Importing schema...");
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("USE " + DB_NAME);
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO'");
            
            InputStream sqlStream = EmbeddedDatabaseManager.class
                .getResourceAsStream("/database/schema.sql");
            
            if (sqlStream == null) {
                System.err.println("Warning: schema.sql not found");
                return;
            }
            
            String sqlContent = new String(sqlStream.readAllBytes());
            String[] statements = sqlContent.split(";");
            
            int success = 0, failed = 0;
            
            for (String sql : statements) {
                sql = sql.trim();
                
                if (sql.isEmpty() || 
                    sql.startsWith("--") || 
                    sql.startsWith("/*") ||
                    sql.toUpperCase().startsWith("SET")) {
                    continue;
                }
                
                try {
                    stmt.execute(sql);
                    success++;
                } catch (SQLException e) {
                    failed++;
                    if (!e.getMessage().toLowerCase().contains("already exists")) {
                        System.err.println("SQL Warning: " + e.getMessage());
                    }
                }
            }
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("Schema imported: " + success + " OK, " + failed + " skipped");
            
            markAsInitialized();
            
        } catch (Exception e) {
            System.err.println("Schema import error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stop embedded database cleanly
     */
    public static void stopEmbeddedDatabase() {
        if (serverProcess != null && serverProcess.isAlive()) {
            System.out.println("Stopping MariaDB...");
            
            // Try graceful shutdown first
            try {
                Connection conn = getEmbeddedConnection();
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SHUTDOWN");
                }
                conn.close();
            } catch (Exception e) {
                // Graceful shutdown failed, force kill
            }
            
            serverProcess.destroy();
            try {
                serverProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
                System.out.println("MariaDB stopped");
            } catch (InterruptedException e) {
                serverProcess.destroyForcibly();
            }
        }
    }
    
    /**
     * Get connection WITHOUT database name (for creation)
     */
    private static Connection getEmbeddedConnectionWithoutDB() throws SQLException {
        String url = "jdbc:mysql://localhost:" + EMBEDDED_PORT + 
                     "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        SQLException lastException = null;
        // Only 3 attempts since we already waited for server to be ready
        for (int i = 0; i < 3; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                System.out.println("✓ Connected to MySQL");
                return conn;
            } catch (SQLException e) {
                lastException = e;
                if (i < 2) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        System.err.println("\n✗ Connection failed after 3 attempts");
        throw lastException;
    }
    
    /**
     * Get connection to database
     */
    public static Connection getEmbeddedConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:" + EMBEDDED_PORT + "/" + DB_NAME + 
                     "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        SQLException lastException = null;
        for (int i = 0; i < 15; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                if (i > 0) {
                    System.out.println("✓ Connected after " + (i + 1) + " attempts");
                }
                return conn;
            } catch (SQLException e) {
                lastException = e;
                if (i < 14) {
                    System.out.print("Waiting... (" + (i + 1) + "/15)\r");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        System.err.println("\nConnection failed after 15 attempts");
        throw lastException;
    }
    
    /**
     * Get data directory
     */
    private static String getDataDirectory() {
        String userHome = System.getProperty("user.home");
        String appData = userHome + File.separator + ".enrollment_system" + File.separator + "data";
        File dir = new File(appData);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return appData;
    }
    
    /**
     * Check if first run
     */
    private static boolean isFirstRun() {
        File initFlag = new File(getDataDirectory() + File.separator + ".initialized");
        return !initFlag.exists();
    }
    
    /**
     * Mark as initialized
     */
    private static void markAsInitialized() {
        try {
            File initFlag = new File(getDataDirectory() + File.separator + ".initialized");
            initFlag.createNewFile();
            System.out.println("✓ Marked initialized");
        } catch (IOException e) {
            System.err.println("Warning: Could not create flag: " + e.getMessage());
        }
    }

    public static void resetDatabase() {
        System.out.println("⚠ Resetting database...");
        killOrphanedMariaDBProcesses();
        String dataDir = getDataDirectory();
        forceCleanDataDirectory(dataDir);
        System.out.println("✓ Reset complete. Restart application.");
    }
}
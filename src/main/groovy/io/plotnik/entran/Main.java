package io.plotnik.entran;

import java.io.File;
import picocli.CommandLine;
import static picocli.CommandLine.*;

import java.util.concurrent.Callable;
import static java.lang.System.out;

@Command(header = {
    "@|cyan                        __                                  |@",
    "@|cyan      ____   ____     _/  |_____________    ____            |@",
    "@|cyan    _/ __ \\ /    \\    \\   __\\_  __ \\__  \\  /    \\    |@",
    "@|cyan    \\  ___/|   |  \\    |  |  |  | \\// __ \\|   |  \\     |@",
    "@|cyan     \\___  >___|  /____|__|  |__|  (____  /___|  /         |@",
    "@|cyan         \\/     \\/_____/                \\/     \\/       |@"
},
        name = "en_tran", mixinStandardHelpOptions = true, version = "1.0",
        description = "Finding matches between the English original and the Russian translation.")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Database name.")
    String databaseName;
    
    @Option(names = {"-e", "--en-file"}, description = "HTML file with English text")
    String enFile = "en/index.html";
    
    @Option(names = {"-r", "--ru-file"}, description = "HTML file with Russian text")
    String ruFile = "ru/index.html";

    // @Option(names = {"-i", "--init-db"}, description = "Initialize database")
    // boolean initDatabase;

    @Option(names = {"-v", "--verbose"}, description = "Verbose mode")
    static boolean verbose;

    @Option(names = {"-d", "--dump"}, description = "Dump debug database")
    static boolean dumpDebugDB;

    @Override
    public Integer call() throws Exception {
        try {
            TranEngine tranEngine = new TranEngine(databaseName, enFile, ruFile);
            // if (initDatabase) {
            //     tranEngine.initDatabase();
            // }

            EnTranFrame.setLookAndFeel("Nimbus");
            EnTranFrame frame = new EnTranFrame();
            frame.setTranEngine(tranEngine);
            tranEngine.setFrame(frame);
            tranEngine.configureButtons();

            frame.pack();
            frame.setVisible(true);

            return 0;

        } catch (Exception e) {
            String msg = e.getMessage();
            if (e instanceof TranException) {
                msg = ((TranException) e).getReason();
            }
            out.println("[ERROR] " + msg);
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    public static void main(String args[]) {
        // For waitUntilClosed see:
        // https://github.com/plotnik/bookindex/blob/master/src/main/groovy/io/github/plotnik/Main.java
        new CommandLine(new Main()).execute(args);
    }
}

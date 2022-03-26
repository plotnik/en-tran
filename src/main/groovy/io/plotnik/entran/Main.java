package io.plotnik.entran;

import picocli.CommandLine;
import static picocli.CommandLine.*;

import java.util.concurrent.Callable;
import static java.lang.System.out;

@Command(header = {
    "@|cyan                        __                                  |@",
    "@|cyan      ____   ____     _/  |_____________    ____            |@",
    "@|cyan    _/ __ \\ /    \\    \\   __\\_  __ \\__  \\  /    \\           |@",
    "@|cyan    \\  ___/|   |  \\    |  |  |  | \\// __ \\|   |  \\          |@",
    "@|cyan     \\___  >___|  /____|__|  |__|  (____  /___|  /          |@",
    "@|cyan         \\/     \\/_____/                \\/     \\/           |@"
    },
    name = "en_tran", mixinStandardHelpOptions = true, version = "1.0",
    description = "Finding matches between the English original and the Russian translation.")
public class Main implements Callable<Integer> {

    public static void main(String args[]) {
        System.exit(new CommandLine(new Main()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        try {
            EnTranFrame.setLookAndFeel("Nimbus");
            EnTranFrame frame = new EnTranFrame();
            return 0;

        } catch (Exception e) {
            String msg = e.getMessage();
            if (e instanceof TranException) {
                msg = ((TranException) e).getReason();
            }
            out.println("[ERROR] " + msg);
            //e.printStackTrace();
            return 1;
        }

    }
}

package uk.ac.ebi.spot.gwas.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.common.util.CommandUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private static final String APP_COMMAND = "java -jar gwas-mapping-service.jar";
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter help = new HelpFormatter();
    private final Options options = CommandUtil.bindOptions();
    private static final String performer = "automatic_mapping_process";

    @Autowired
    private EnsemblRunnner ensemblRunnner;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {

        CommandLine commandLine = parser.parse(options, args, true);
        boolean mode = commandLine.hasOption(CommandUtil.EXEC_MODE_OPT);
        List<String> argList = commandLine.getArgList();

        if (mode) {
            String execMode = (argList.size() > 0) ? argList.get(0) : "";
            int threadSize = (argList.size() > 1) ? Integer.parseInt(argList.get(1)) : 40;
            this.menuDecision(execMode, threadSize);
        } else {
            help.printHelp(130, APP_COMMAND,  "", options,"");
            System.exit(1);
        }
    }


    public void menuDecision(String executionMode, int threadSize) throws InterruptedException, ExecutionException, IOException {
        switch (executionMode) {
            case "map-all-snp":
                log.info("Mapping -r {}", executionMode);
                ensemblRunnner.mapAllAssociations(performer);
                System.exit(1);
                break;
            case "map-some-snp":
                log.info("Mapping -m {}", executionMode);
                ensemblRunnner.mapSomeAssociations(performer);
                System.exit(1);
                break;
            case "cache-ensembl-data":
                ensemblRunnner.runCache(threadSize);
                System.exit(1);
                break;
            case "server-mode":
                log.info("Application executed successfully, running in server mode!");
                break;
            default:
                log.info("The mode value {} is not recognized", executionMode);
                help.printHelp(APP_COMMAND, options, true);
                System.exit(1);
        }
    }
}


// -m automatic_mapping_process 40

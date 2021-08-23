package uk.ac.ebi.spot.gwas;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.constant.OperationMode;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblRunnner;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private static final String APP_COMMAND = "java -jar gwas-mapping-service.jar -m automatic_mapping_process";
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter help = new HelpFormatter();
    private final Options options = CommandUtil.bindOptions();

    @Autowired
    private EnsemblRunnner ensemblRunnner;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
        log.info("Starting mapping service...");

        CommandLine commandLine = parser.parse(options, args, true);
        boolean viewHelp = commandLine.hasOption(CommandUtil.HELP_OPT);
        boolean runCache = commandLine.hasOption(CommandUtil.CACHE_OPT);
        boolean mapSomeSNPs = commandLine.hasOption(CommandUtil.MAP_SOME_SNPS_INDB_OPT);
        boolean remapAllSNPs = commandLine.hasOption(CommandUtil.MAP_ALL_SNPS_INDB_OPT);
        String performer = String.valueOf(commandLine.getArgList().get(0));

        if (viewHelp) {
            help.printHelp(APP_COMMAND, options, true);

        } else if (remapAllSNPs) {
            ensemblRunnner.mapAssociations(OperationMode.MAP_ALL_SNPS_INDB);

        } else if (mapSomeSNPs) {
            ensemblRunnner.mapAssociations(OperationMode.MAP_SOME_SNPS_INDB);
            log.info("Night -n {}", performer);

        } else if (runCache) {
            int threadSize = Integer.parseInt(commandLine.getArgList().get(0));
            ensemblRunnner.runCache(threadSize);

        }
        log.info("Application executed successfully!");
    }

}


//        try {
//        } catch (Exception e) {
//            log.info("No argument was supplied ( {} )", e.getMessage());
//            help.printHelp(APP_COMMAND, options, true);
//        }


// save History
// full db Mapping
//

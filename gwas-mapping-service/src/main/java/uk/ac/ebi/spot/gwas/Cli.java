package uk.ac.ebi.spot.gwas;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.controller.MappingController;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;


@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private static final String APP_COMMAND = "java -jar gwas-mapping-service.jar";
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter help = new HelpFormatter();
    private final Options options = CommandUtil.bindOptions();

    @Autowired
    private EnsemblService ensemblService;

    @Autowired
    private MappingController mappingController;

    @Override
    public void run(String... args) throws ParseException {
        log.info("Starting mapping service...");

        try {
            CommandLine commandLine = parser.parse(options, args, true);

            boolean viewHelp = commandLine.hasOption(CommandUtil.HELP_OPT);
            boolean runMapping = commandLine.hasOption(CommandUtil.MAPPING_OPT);
            boolean runCache = commandLine.hasOption(CommandUtil.CACHE_OPT);
            boolean runNight = commandLine.hasOption(CommandUtil.NIGHT_OPT);
            String performer = String.valueOf(commandLine.getArgList().get(0));

            if (viewHelp) {
                help.printHelp(APP_COMMAND, options, true);

            } else if (runMapping) {
                Object report = ensemblService.fullEnsemblRemapping();
                log.info(String.valueOf(report));

            } else if (runNight) {
                log.info("Night -n {}", performer);

            }else if (runCache) {
                int threadSize = Integer.parseInt(commandLine.getArgList().get(0));
                log.info("Caching Ensembl with {} thread size", threadSize);
                Object report = ensemblService.cacheEnsemblData(threadSize);
                log.info(String.valueOf(report));
            }
        } catch (Exception e) {
            log.info("No argument was supplied ( {} )", e.getMessage());
            help.printHelp(APP_COMMAND, options, true);
        }

        log.info("Application executed successfully!");
    }


}


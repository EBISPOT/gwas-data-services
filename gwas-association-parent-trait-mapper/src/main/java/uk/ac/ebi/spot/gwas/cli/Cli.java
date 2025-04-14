package uk.ac.ebi.spot.gwas.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.service.EFOLoaderService;
import uk.ac.ebi.spot.gwas.service.EFOTraitService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private final Logger msubLog = LoggerFactory.getLogger("msublogger");

    private static String executionMode = null;

    private static String parentEfo = null;

    private static String efoIds = null;

    @Autowired
    EFOTraitService efoTraitService;

    @Autowired
    EFOLoaderService efoLoaderService;


    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
        CommandLine commandLine = parseArguments(args);
        Boolean mode = commandLine.hasOption(CommandUtil.EXEC_EFOTRAITS);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        log.info("Parent Trait mapping  started at {}",dateFormat.format(new Date()));
        long start = System.currentTimeMillis();
        if (mode) {

            log.info("executionMode is {}",executionMode);
            if(executionMode.equalsIgnoreCase("full") || executionMode.equalsIgnoreCase("file")) {
                try {
                    Map<String, List<String>> efoParentChildMap = efoTraitService.loadParentChildEfo(Arrays.asList(efoIds.split(",")));
                    List<EfoTrait> efoTraits = efoTraitService.saveParentEFOMapping(efoParentChildMap);
                    efoLoaderService.loadAssociationsWithParentEfo(efoTraits);
                    efoLoaderService.loadStudiesWithParentEfo(efoTraits);
                } catch (Exception ex) {
                    msubLog.error("EFoId's failed to run for the following {}", efoIds);
                    log.error("Execution Mapper failed for the following EfoIds"+ex.getMessage(),ex);
                    throw ex;
                }
            }
            if(executionMode.equalsIgnoreCase("childefos")) {
                try {
                    efoLoaderService.loadAssociationsForChildEfos(Arrays.asList(efoIds.split(",")), parentEfo);
                    efoLoaderService.loadStudiesForChildEfos(Arrays.asList(efoIds.split(",")), parentEfo);
                } catch (Exception ex) {
                    msubLog.error("EFoId's failed to run for the following {}", efoIds);
                    log.error("Execution Mapper failed for the following EfoIds"+ex.getMessage(),ex);
                    throw ex;
                }
            }

            if(executionMode.equalsIgnoreCase("largeefos")) {
                try {
                    Map<String, List<String>> efoParentChildMap = efoTraitService.loadParentChildEfo(Arrays.asList(efoIds.split(",")));
                    List<EfoTrait> efoTraits = efoTraitService.saveParentEFOMapping(efoParentChildMap);
                    efoLoaderService.runDataForLargeEfo(efoTraits);
                } catch (Exception ex) {
                    msubLog.error("EFoId's failed to run for the following {}", efoIds);
                    log.error("Execution Mapper failed for the following EfoIds"+ex.getMessage(),ex);
                    throw ex;
                }
            }

            log.info("Total Parent Trait mapping time {}", (System.currentTimeMillis() - start));
            log.info("Parent Trait mapping ended at {}",dateFormat.format(new Date()));
        } else {
            System.err.println("Insufficient params ");
            System.exit(1);
        }
    }

    private CommandLine parseArguments(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);

            if (cl.hasOption("e")) {
                // print out mode help
                log.info("Inside -e option");
                efoIds = cl.getOptionValue("e");
            }
            if (cl.hasOption("m")) {
                // print out mode help
                log.info("Inside -m option");
                executionMode = cl.getOptionValue("m");
            }
            if (cl.hasOption("p")) {
                // print out mode help
                log.info("Inside -p option");
                parentEfo = cl.getOptionValue("p");
            }
        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);

        }
        return cl;
    }

}

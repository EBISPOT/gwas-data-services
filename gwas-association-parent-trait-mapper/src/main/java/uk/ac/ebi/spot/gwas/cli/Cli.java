package uk.ac.ebi.spot.gwas.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.service.EFOTraitService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private static String executionMode = null;

    private static String[] efoIds = null;

    @Autowired
    EFOTraitService efoTraitService;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
        CommandLine commandLine = parseArguments(args);
        Boolean mode = commandLine.hasOption(CommandUtil.EXEC_EFOTRAITS);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        log.info("Parent Trait mapping  started at {}",dateFormat.format(new Date()));
        long start = System.currentTimeMillis();
        if (mode) {
            Map<String, List<String>> efoParentChildMap = efoTraitService.loadParentChildEfo(Arrays.asList(efoIds));
            efoTraitService.saveParentEFOMapping(efoParentChildMap);
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
                efoIds = cl.getOptionValues("e");
            }
        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);

        }
        return cl;
    }

}

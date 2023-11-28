package uk.ac.ebi.spot.gwas.cli;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.service.AssociationService;
import uk.ac.ebi.spot.gwas.service.FileHandlerService;
import uk.ac.ebi.spot.gwas.service.MappingJobSubmitterService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class Cli implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    private static String[] rsIds = null;
    private final HelpFormatter help = new HelpFormatter();
    private static String outputDir = null;
    private static String errorDir = null;

    private static String inputDir = null;

    private static String executionMode = null;

    @Autowired
    AssociationService associationService;


    @Autowired
    MappingJobSubmitterService mappingJobSubmitterService;

    @Autowired
    FileHandlerService fileHandlerService;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
       // CommandLine commandLine = parser.parse(options, args, true);
        CommandLine commandLine = parseArguments(args);
        Boolean mode = commandLine.hasOption(CommandUtil.EXEC_MODE_OPT);
        DateFormat dateFormat = new SimpleDateFormat(   "yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        bsubLog.info("Mapping Pipeline started at {}",dateFormat.format(date));
        long start = System.currentTimeMillis();
        if (mode) {
            List<Long> asscns = null;
            log.info("Execution mode is ->"+executionMode);
            if(executionMode.equals("file")) {
                List<String> rsIdtoMap = fileHandlerService.readFileInput(inputDir);
                asscns = rsIdtoMap.stream().map(associationService::getAssociationBasedOnRsId)
                        .map(associations -> associations.stream()
                                .map(Association::getId)
                                .collect(Collectors.toList()))
                        .flatMap(asscnIds ->asscnIds.stream())
                        .collect(Collectors.toList());
            } else if(executionMode.equals("schedule")) {
                associationService.scheduledRemapping(outputDir, errorDir);
            } else if(executionMode.equals("full")) {
                associationService.fullRemapping(outputDir, errorDir);
            }else if(executionMode.equals("mapping-error-list")) {
                associationService.findAssociationMappingError();
            } else {
                asscns = Arrays.asList(rsIds).stream()
                        .map(associationService::getAssociationBasedOnRsId)
                        .map(associations ->
                                associations.stream()
                                        .map(Association::getId)
                                        .collect(Collectors.toList()))
                        .flatMap(asscnIds -> asscnIds.stream())
                        .collect(Collectors.toList());
            }
            if( !executionMode.equals("full") && !executionMode.equals("schedule") && !executionMode.equals("mapping-error-list")) {
                associationService.updateMappingDetails(asscns);
                mappingJobSubmitterService.executePipeline(asscns, outputDir, errorDir, "executor-1");
                log.info("Total Association count to map is {}", asscns.size());
            }
            log.info("Mapping Pipeline took {} ms", (System.currentTimeMillis()- start));
            bsubLog.info("Mapping Pipeline took {} ms", (System.currentTimeMillis()- start));
            Date endDate = new Date();
            bsubLog.info("Mapping Pipeline ended at {}",dateFormat.format(endDate));
        }else{
            System.err.println("Insufficient params ");
            System.exit(1);
        }
    }

    private CommandLine  parseArguments(String[] args) {

        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);

            if (cl.hasOption("h")) {
                // print out mode help
                help.printHelp("run-mapping-pipeline.sh", options, true);

            }
            if (cl.hasOption("r")) {
                // print out mode help
                log.info("Inside -r option");
                rsIds = cl.getOptionValues("r");
            }

            if (cl.hasOption("m")) {
                log.info("Inside -m option");
                //input file Dire
                executionMode = cl.getOptionValue("m");
            }

            if (cl.hasOption("i")) {
                //input file Dire
                log.info("Inside -i option");
                inputDir = cl.getOptionValue("i");
            }

            if (cl.hasOption("o")) {
                //output file for Bjob
                log.info("Inside -o option");
                outputDir = cl.getOptionValue("o");
            }
            if (cl.hasOption("e")) {
                //error file for Bjob
                log.info("Inside -e option");
                errorDir = cl.getOptionValue("e");
            }
        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);

        }

        return cl;
    }



}
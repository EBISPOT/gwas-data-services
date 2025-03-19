package uk.ac.ebi.spot.gwas.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.service.ParentMapperService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class Cli implements CommandLineRunner {

    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    private static String executionMode = null;
    private final HelpFormatter help = new HelpFormatter();
    private static String outputDir = null;
    private static String errorDir = null;

    @Autowired
    ParentMapperService parentMapperService;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
        parseArguments(args);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        long start = System.currentTimeMillis();
        parentMapperService.executeParentMapper(outputDir, errorDir);
        bsubLog.info("Association Mapper Executor started at {}",dateFormat.format(date));
        log.info("Association Mapper Executor took {} ms", (System.currentTimeMillis()- start));
        bsubLog.info("Association Mapper Executor took {} ms", (System.currentTimeMillis()- start));
        Date endDate = new Date();
        bsubLog.info("Association Mapper Executor ended at {}",dateFormat.format(endDate));
    }

    private CommandLine parseArguments(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);
            if (cl.hasOption("h")) {
                // print out mode help
                help.printHelp("run-slurm-parent-trait-mapper", options, true);

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

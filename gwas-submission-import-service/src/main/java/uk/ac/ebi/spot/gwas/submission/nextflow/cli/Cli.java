package uk.ac.ebi.spot.gwas.submission.nextflow.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SubmissionImportProgressService;
import uk.ac.ebi.spot.gwas.submission.nextflow.util.CommandUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class Cli implements CommandLineRunner {
    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    SubmissionImportProgressService submissionImportProgressService;

    private String studyIds = null;
    private String submissionId = null;
    private String submissionType = null;
    private String curatorEmail = null;
    private String pmid = null;


    public void run(String... args) throws Exception {
        CommandLine commandLine = parseArguments(args);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        bsubLog.info("Submission Nextflow job started at {}", dateFormat.format(date));
        long start = System.currentTimeMillis();
    }

    private CommandLine parseArguments(String[] args) {

        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);
            if (cl.hasOption("g")) {
                log.info("Inside -g option");
                studyIds = cl.getOptionValue("g");
            }
            if (cl.hasOption("s")) {
                log.info("Inside -s option");
                submissionId = cl.getOptionValue("g");
            }
            if (cl.hasOption("t")) {
                log.info("Inside -t option");
                submissionType = cl.getOptionValue("t");
            }
            if(cl.hasOption("c")) {
                log.info("Inside -c option");
                curatorEmail = cl.getOptionValue("c");
            }
            if(cl.hasOption("p")) {
                log.info("Inside -p option");
                pmid = cl.getOptionValue("p");
            }

        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);

        }

        return cl;
    }
}

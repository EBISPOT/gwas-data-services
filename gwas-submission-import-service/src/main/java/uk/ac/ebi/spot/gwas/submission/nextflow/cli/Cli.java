package uk.ac.ebi.spot.gwas.submission.nextflow.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.constants.SubmissionType;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SubmissionImportProgressService;
import uk.ac.ebi.spot.gwas.submission.nextflow.util.CommandUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class Cli implements CommandLineRunner {
    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    SubmissionImportProgressService submissionImportProgressService;
    

    private String studyIds = null;
    private String submissionId = null;
    private String submissionType = null;
    private String curatorEmail = null;
    private String pmid = null;
    private String mode = null;


    public void run(String... args) throws Exception {
        parseArguments(args);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        bsubLog.info("Submission Nextflow job started at {}", dateFormat.format(date));
        try {
                Integer studiesImported = 0;
                long start = System.currentTimeMillis();
                for (String studyId : studyIds.split("_")) {
                    log.info("StudyId is {}", studyId);
                }
                if (submissionType.equals(SubmissionType.SUMMARY_STATS.name())) {
                    studiesImported = submissionImportProgressService.publishSummaryStats(submissionId, Arrays.asList(studyIds.split("_")), pmid);
                } else {
                    studiesImported = submissionImportProgressService.importSubmission(submissionId, Arrays.asList(studyIds.split("_")), curatorEmail, pmid);
                }
                submissionImportProgressService.savePmidReporting(submissionId, studiesImported);
                log.info("Total time taken to import {}", System.currentTimeMillis() - start);
        } catch(Exception ex) {
            log.error("Exception in import submission"+ex.getMessage(),ex);
            throw ex;
        }


    }

    private CommandLine parseArguments(String[] args) throws Exception{

        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);
            if (cl.hasOption("g")) {
                log.info("Inside -g option");
                studyIds = cl.getOptionValue("g");
                log.info("studyIds is {}", studyIds);
            }
            if (cl.hasOption("s")) {
                log.info("Inside -s option");
                submissionId = cl.getOptionValue("s");
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

            if(cl.hasOption("m")) {
                log.info("Inside -m option");
                mode = cl.getOptionValue("m");
            }


        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);

        }

        return cl;
    }
}

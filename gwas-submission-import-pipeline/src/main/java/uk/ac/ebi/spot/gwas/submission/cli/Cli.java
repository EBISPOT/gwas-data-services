package uk.ac.ebi.spot.gwas.submission.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.constants.PmidReportingStatus;
import uk.ac.ebi.spot.gwas.deposition.constants.Status;
import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.rest.projection.StudyAccessionIdProjection;
import uk.ac.ebi.spot.gwas.submission.constants.DepositionCurationConstants;
import uk.ac.ebi.spot.gwas.submission.service.*;
import uk.ac.ebi.spot.gwas.submission.util.CommandUtil;
import uk.ac.ebi.spot.gwas.submission.util.EmailHelperUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Cli implements CommandLineRunner {


    @Autowired
    SubmissionImportService submissionImportService;

    @Autowired
    SubmissionService submissionService;


    @Autowired
    EmailHelperUtil emailHelperUtil;

    @Autowired
    StudiesService studiesService;

    @Autowired
    AssociationService associationService;

    @Autowired
    UnpublishedStudiesService unpublishedStudiesService;

    @Autowired
    PmidImportReportingService pmidImportReportingService;

    private String submissionId = null;
    private String curatorEmail = null;
    private String pmid = null;


    public void run(String... args) throws Exception {
        parseArguments(args);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Pair<String,String> emailContent = null;
        String status = null;
        log.info("Submission Nextflow job started at {}", dateFormat.format(date));
        Boolean failed = false;
        long start = System.currentTimeMillis();
        log.info("Calling importSubmission() start");
        List<PmidImportReporting> pmidImportReportingsInProgress = pmidImportReportingService.findByStatus(PmidReportingStatus.IMPORT_IN_PROGRESS.name());
        if(pmidImportReportingsInProgress != null && !pmidImportReportingsInProgress.isEmpty()) {
            if(pmidImportReportingsInProgress.size() > 1) {
                log.error("Submission Import limit reached , Please wait for some time until one of the submission finishes");
                throw new SlurmProcessException("Submission Import limit reached , Please wait for some time one of the submission finishes");
            }
        }

        List<PmidImportReporting> pmidImportReportings = pmidImportReportingService.findByStatus(PmidReportingStatus.IMPORT_PENDING.name());
        //for(List<PmidImportReporting> pmidImportReportingPartition : ListUtils.partition(pmidImportReportings, 2)) {
            List<PmidImportReporting> updatedPmidImportReportingPartition = new ArrayList<>();
            for(PmidImportReporting pmidImportReporting : pmidImportReportings) {
                try {
                    submissionId = pmidImportReporting.getSubmissionId();
                    curatorEmail = pmidImportReporting.getCuratorEmail();
                    Long totalStudies = studiesService.findBySubmissionId(pmidImportReporting.getSubmissionId());
                    Long associationsTotal = associationService.findBySubmissionId(pmidImportReporting.getSubmissionId());
                    pmidImportReporting.setStatus(PmidReportingStatus.IMPORT_IN_PROGRESS.name());
                    pmidImportReporting.setStudiesTotal(totalStudies.intValue());
                    pmidImportReporting.setAssociationTotal(associationsTotal.intValue());
                    pmidImportReporting.setStartDate(new Date());
                    log.info("Calling pmidImportReporting with status  IN_PROGRESS");
                    updatedPmidImportReportingPartition.add(submissionImportService.savePmidReporting(pmidImportReporting));
                }catch(Exception ex) {
                    log.error("Exception in import submission"+ex.getMessage(),ex);
                    failed = true;
                    status = DepositionCurationConstants.IMPORT_FAILED;
                    submissionImportService.savePmidReporting(submissionId, PmidReportingStatus.IMPORT_FAILED.name());
                    emailContent = emailHelperUtil.getBody(pmid, submissionId, status);
                    log.info("Sending Email content start");
                    emailHelperUtil.sendMessage(emailContent.getLeft(), emailContent.getRight());
                    log.info("Sending Email content end");
                }
            }
            updatedPmidImportReportingPartition.sort(Comparator.comparing(PmidImportReporting::getStudiesTotal));
            for(List<PmidImportReporting> pmidImportReportingPartition : ListUtils.partition(updatedPmidImportReportingPartition, 2)) {
            for (PmidImportReporting pmidImportReporting : pmidImportReportingPartition) {
                try {
                    submissionId = pmidImportReporting.getSubmissionId();
                    curatorEmail = pmidImportReporting.getCuratorEmail();
                    pmid = pmidImportReporting.getPublication().getPubmedId();
                    List<StudyAccessionIdProjection> studyProjections = studiesService.findAccessionIdsByPubmedId(pmid);
                    List<Long> studyIds = studyProjections.stream().map(StudyAccessionIdProjection::getId).collect(Collectors.toList());
                    log.info("Start of deleteStudies() start");
                    studiesService.deleteStudies(studyIds);
                    log.info("Start of deleteStudies() end");
                    log.info("submissionId in import pipeline is {}  Curator mail is {}", submissionId, curatorEmail);
                    submissionImportService.importSubmission(submissionId, curatorEmail);
                    log.info("Calling importSubmission() end");
                    log.info("Calling updateSubmissionStatus() start");
                    submissionService.updateSubmissionStatus(submissionId, Status.CURATION_COMPLETE.name(), curatorEmail);
                    log.info("Calling pmidImportReporting with status  COMPLETED");
                    submissionImportService.savePmidReporting(submissionId, PmidReportingStatus.IMPORT_COMPLETED.name());
                    log.info("Calling updateSubmissionStatus() end");
                    status = Status.CURATION_COMPLETE.name();
                    List<String> accessionIds = studyProjections.stream().map(StudyAccessionIdProjection::getAccessionId).collect(Collectors.toList());
                    log.info("Calling cleanUpUnpublishedStudies() start");
                    unpublishedStudiesService.cleanUpUnpublishedStudies(accessionIds);
                    log.info("Calling cleanUpUnpublishedStudies() end");
                    log.info("Calling deleteSubmissionInProgressEntry() start");
                    submissionImportService.deleteSubmissionInProgressEntry(submissionId);
                    log.info("Calling deleteSubmissionInProgressEntry() end");
                    log.info("Total time taken to import {} {}", submissionId,  System.currentTimeMillis() - start);
                }catch(SlurmProcessException ex) {
                    failed = true;
                    submissionService.updateSubmissionStatus(submissionId, DepositionCurationConstants.IMPORT_FAILED, curatorEmail);
                    submissionImportService.savePmidReporting(submissionId, PmidReportingStatus.IMPORT_FAILED.name());
                    status = DepositionCurationConstants.IMPORT_FAILED;
                    log.error("SlurmProcessException in import submission"+ex.getMessage(),ex);
                } catch(Exception ex) {
                    failed = true;
                    submissionService.updateSubmissionStatus(submissionId, DepositionCurationConstants.IMPORT_FAILED, curatorEmail);
                    submissionImportService.savePmidReporting(submissionId, PmidReportingStatus.IMPORT_FAILED.name());
                    status = DepositionCurationConstants.IMPORT_FAILED;
                    log.error("Exception in import submission"+ex.getMessage(),ex);
                }
                finally {
                    emailContent = emailHelperUtil.getBody(pmid, submissionId, status);
                    log.info("Sending Email content start");
                    emailHelperUtil.sendMessage(emailContent.getLeft(), emailContent.getRight());
                    log.info("Sending Email content end");
                }
            }
        }
        if(failed) {
            throw new SlurmProcessException("SlurmProcessException in importing submissions");
        }
    }


    private CommandLine parseArguments(String[] args) throws Exception{

        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        Options options = CommandUtil.bindOptions();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args, true);

            if (cl.hasOption("s")) {
                log.info("Inside -s option");
                submissionId = cl.getOptionValue("s");
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

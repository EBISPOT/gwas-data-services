package uk.ac.ebi.spot.gwas.cli;

import org.apache.commons.cli.*;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.service.*;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    PmidReportingService pmidReportingService;

    @Autowired
    StudiesService studiesService;

    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException, Exception {
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
            }else if(executionMode.equals("auto-import") || executionMode.equals("approve-snps") || executionMode.equals("publish-studies")) {
                Boolean failed = false;
                List<PmidImportReporting> pmidImportReportingsInProgress = null;
                if(executionMode.equals("auto-import")) {
                    pmidImportReportingsInProgress = pmidReportingService.findByStatus("MAPPING_IN_PROGRESS");
                } else if(executionMode.equals("publish-studies")) {
                    pmidImportReportingsInProgress = pmidReportingService.findByStatus("PUBLISH_IN_PROGRESS");
                } else {
                    pmidImportReportingsInProgress = pmidReportingService.findByStatus("SNP_APPROVAL_IN_PROGRESS");
                }
                if(pmidImportReportingsInProgress != null && !pmidImportReportingsInProgress.isEmpty()) {
                    if(pmidImportReportingsInProgress.size() > 1) {
                        if(executionMode.equals("auto-import")) {
                            log.error("Mapping  limit reached , Please wait for some time until one of the mapping finishes");
                            throw new SlurmProcessException("Mapping  limit reached , Please wait for some time one of the submission finishes");
                        }else if(executionMode.equals("publish-studies")) {
                            log.error("Publish Studies limit reached , Please wait for some time until one of the mapping finishes");
                            throw new SlurmProcessException("Publish Studie limit reached , Please wait for some time one of the submission finishes");
                        }else {
                            log.error("Snp Approval  limit reached , Please wait for some time until one of the Snp Approval finishes");
                            throw new SlurmProcessException("Snp Approval limit reached , Please wait for some time one of the submission finishes");
                        }
                    }
                }
                List<PmidImportReporting> pmidImportReportings = null;
                if(executionMode.equals("auto-import")) {
                    pmidImportReportings = pmidReportingService.findByStatus("IMPORT_COMPLETED");
                }else if(executionMode.equals("publish-studies")) {
                    pmidImportReportings = pmidReportingService.findByStatus("SNP_APPROVAL_COMPLETED");
                }  else {
                    pmidImportReportings = pmidReportingService.findByStatus("MAPPING_COMPLETED");
                }
                for(List<PmidImportReporting> pmidImportReportingPartition : ListUtils.partition(pmidImportReportings, 2)) {
                    List<PmidImportReporting> updatedPmidImportReportingPartition = new ArrayList<>();
                    for (PmidImportReporting pmidImportReporting : pmidImportReportingPartition) {
                        if(executionMode.equals("auto-import")) {
                            pmidImportReporting.setStatus("MAPPING_IN_PROGRESS");
                        }else if(executionMode.equals("publish-studies")) {
                            pmidImportReporting.setStatus("PUBLISH_IN_PROGRESS");
                        }else {
                            pmidImportReporting.setStatus("SNP_APPROVAL_IN_PROGRESS");
                        }
                        updatedPmidImportReportingPartition.add(associationService.savePmidReporting(pmidImportReporting));
                    }
                    if(executionMode.equals("auto-import") || executionMode.equals("approve-snps")) {
                        updatedPmidImportReportingPartition.sort(Comparator.comparing(PmidImportReporting::getAssociationTotal));
                    } else {
                        updatedPmidImportReportingPartition.sort(Comparator.comparing(PmidImportReporting::getStudiesTotal));
                    }
                    for (PmidImportReporting pmidImportReporting : updatedPmidImportReportingPartition) {
                        String pmid = pmidImportReporting.getPublication().getPubmedId();
                        String submissionId = pmidImportReporting.getSubmissionId();
                        log.info("Pmid to map assciation is {}", pmid);
                        try {
                            if(executionMode.equals("auto-import") || executionMode.equals("approve-snps")) {
                                associationService.mapAssociationsBasedOnPmid(pmid, submissionId, outputDir, errorDir, executionMode);
                            } else {
                                studiesService.publishStudiesForPmid(pmid, submissionId, outputDir, errorDir, executionMode);
                            }
                            if(executionMode.equals("auto-import")) {
                                associationService.savePmidReporting(submissionId, "MAPPING_COMPLETED");
                            } else if(executionMode.equals("publish-studies")) {
                                associationService.savePmidReporting(submissionId, "PUBLISH_COMPLETED");
                            } else {
                                associationService.savePmidReporting(submissionId, "SNP_APPROVAL_COMPLETED");
                            }
                        } catch (SlurmProcessException ex) {
                            failed = true;
                            if(executionMode.equals("auto-import")) {
                                associationService.savePmidReporting(submissionId, "MAPPING_FAILED");
                                log.error("SlurmProcessException in mapping pipeline "+ex.getMessage(),ex);
                            } else if(executionMode.equals("publish-studies")) {
                                associationService.savePmidReporting(submissionId, "PUBLISH_FAILED");
                                log.error("SlurmProcessException in published studies "+ex.getMessage(),ex);
                            } else {
                                associationService.savePmidReporting(submissionId, "SNP_APPROVAL_FAILED");
                                log.error("SlurmProcessException in snp approval "+ex.getMessage(),ex);
                            }

                        } catch (Exception ex) {
                            failed = true;
                            if(executionMode.equals("auto-import")) {
                                associationService.savePmidReporting(submissionId, "MAPPING_FAILED");
                                log.error("SlurmProcessException in mapping pipeline "+ex.getMessage(),ex);
                            }else if(executionMode.equals("publish-studies")) {
                                associationService.savePmidReporting(submissionId, "PUBLISH_FAILED");
                                log.error("SlurmProcessException in published studies "+ex.getMessage(),ex);
                            }
                            else{
                                associationService.savePmidReporting(submissionId, "SNP_APPROVAL_FAILED");
                                log.error("SlurmProcessException in snp approval "+ex.getMessage(),ex);
                            }
                            log.error("Exception in Snp Approval"+ex.getMessage(),ex);
                        }
                    }
                }
                if(failed) {
                    throw new SlurmProcessException("SlurmProcessException in importing submissions");
                }
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
            if( !executionMode.equals("full") && !executionMode.equals("schedule")
                    && !executionMode.equals("mapping-error-list") && !executionMode.equals("auto-import")
                    && !executionMode.equals("approve-snps") && !executionMode.equals("publish-studies")) {
                associationService.updateMappingDetails(asscns);
                mappingJobSubmitterService.executePipeline(asscns, outputDir, errorDir, "executor-1", executionMode, null);
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
package uk.ac.ebi.spot.gwas.submission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.submission.config.NextFlowJobConfig;
import uk.ac.ebi.spot.gwas.submission.service.NextflowSubmitterService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NextflowSubmitterServiceImpl implements NextflowSubmitterService {

    NextFlowJobConfig nextFlowJobConfig;
    int retryCount = 0;

    public NextflowSubmitterServiceImpl(NextFlowJobConfig nextFlowJobConfig) {
        this.nextFlowJobConfig = nextFlowJobConfig;
    }

    public void executePipeline(String pmid, String submissionId) throws SlurmProcessException {
        String command = String.format("nextflow -log %s/nextflow_%s.log run %s --job_map_file %s/job_map_%s.csv", nextFlowJobConfig.getSlurmLogsLocation(),
                 pmid, nextFlowJobConfig.getSlurmJobScript(), nextFlowJobConfig.getSlurmLogsLocation(), pmid);
        long start = System.currentTimeMillis();
        String retryCommand = command.concat(" ").concat("-resume");
        if (nextFlowJobConfig.getActiveProfile().equals("local")) {
            log.info("Command for Nextflow is {}", command);
            log.info("Retry Command for Nextflow is {}", retryCommand);
        } else {
            log.info("Command for Nextflow is {}", command);
            fireNextflowCommand(command);
            log.info("Submission Import Nexflow Job took {} ms", (System.currentTimeMillis() - start));
        }
    }


    private void retryCommand(String command) throws SlurmProcessException{

        int maxTries = nextFlowJobConfig.getRetries();
        if(!command.contains("resume")){
            command =  command.concat(" ").concat("-resume");
        }
        while (retryCount <= maxTries) {
                if(retryCount == maxTries) {
                    log.error("Max retries of nextflow pipeline reached");
                    throw new SlurmProcessException("Nextflow returned error status");
                }
                log.info("Retry counter for nextflow pipeline is {}", retryCount);
                fireNextflowCommand(command);
        }
    }


    private void fireNextflowCommand(String command) throws SlurmProcessException {

        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((str = bufferedReader.readLine()) != null) {
                log.info("Process returned this message" + str);
            }
            int exitCode = process.waitFor();
            log.info("Exit code for the Process" + exitCode);
            if(exitCode != 0) {
                throw new SlurmProcessException("Nextflow returned error status");
            }

        }catch (Exception ex) {
            retryCount++;
            long waitTime = (long) Math.pow(2, retryCount) * 100;
            log.error("Deadlock occurred in savePmidReporting" + ex.getMessage(), ex);
            try {
                TimeUnit.MILLISECONDS.sleep(waitTime);
                retryCommand(command);
            } catch (InterruptedException e) {
                log.error("InterruptedException in savePmidReporting" + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            log.error("Exception in the Slurm import submission process"+ex.getMessage(),ex);
        }
    }

}

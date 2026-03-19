package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.Config;
import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.service.MappingJobSubmitterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class MappingJobSubmitterServiceImpl implements MappingJobSubmitterService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");
    //private LsfSubCommand subCmd = new LsfSubCommand();

    private final Integer partitionSize = 2;

    @Autowired
    Config config;

    public void executePipeline(List<Long> asscnIds, String outDir, String errorDir, String executorPool, String mode, String submissionId)  {
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadPool());
        String activeProfile = config.getActiveProfile();
        log.info("Active profile is ->"+activeProfile);
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat(   "yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        List<Future> futures = new ArrayList<>();
        int partitionSize = config.getPartitionSize();
        if(mode.equals("approve-snps") || mode.equals("publish-studies")) {
            partitionSize = 1000;
        }
        bsubLog.info("Bsub for Mapping Pipeline started at {}",dateFormat.format(date));
        for(List<Long> partAsscnIds : ListUtils.partition(asscnIds, partitionSize)) {

            List<String> partAssociations = partAsscnIds.stream().map(String::valueOf).collect(Collectors.toList());
            if(activeProfile.equals("cluster")) {

                futures.add(poolExecutor.submit(() -> {
                    try {
                        String command = "";
                        String slurmOutputFile = "";
                        String slurmErrFile = "";
                        log.info("partAssociations is ->" + partAssociations.stream().collect(Collectors.joining(",")));
                        if(mode.equals("auto-import") || mode.equals("approve-snps") || mode.equals("publish-studies")) {
                            slurmOutputFile = String.format("%s %s/%s/%s/%s", "-o", config.getPmidSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()), "output.log");
                            slurmErrFile = String.format("%s %s/%s/%s/%s", "-e", config.getPmidSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()),"error.log");
                        } else {
                            slurmOutputFile = String.format("%s %s/%s/%s/%s", "-o", config.getSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()), "output.log");
                            slurmErrFile = String.format("%s %s/%s/%s/%s", "-e", config.getSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()), "error.log");
                        }
                        if(mode.equals("auto-import") || mode.equals("approve-snps") || mode.equals("publish-studies")) {
                            command = String.format("%s %s %s %s %s %s %s %s %s %s", "sbatch", slurmOutputFile, slurmErrFile, "--wait", config.getPmidScript(), Math.abs(partAsscnIds.hashCode()), partAssociations.stream().collect(Collectors.joining(",")), executorPool, submissionId , mode);
                        } else {
                            command = String.format("%s %s %s %s %s %s %s %s", "sbatch", slurmOutputFile, slurmErrFile, "--wait", config.getScript(), Math.abs(partAsscnIds.hashCode()), partAssociations.stream().collect(Collectors.joining(",")), executorPool);
                        }
                        log.info("COmmand is ->"+command);
                        Process process = Runtime.getRuntime().exec(command);
                        String str = "";
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        while((str = bufferedReader.readLine()) != null){
                            log.info("Process returned this message"+str);
                            bsubLog.info("Process returned this message"+str);
                        }
                        int exitCode = process.waitFor();
                        if(exitCode != 0) {
                            throw new SlurmProcessException("Nextflow returned error status");
                        }
                        log.info("Exit code for the Process"+exitCode);
                        bsubLog.info("Exit code for the Process"+exitCode);
                    }catch (IOException ex) {
                        log.error(" IOException" + ex.getMessage(), ex);
                    }catch (InterruptedException ex) {
                        log.error(" InterruptedException" + ex.getMessage(), ex);
                    }
                }));
            } else {
                poolExecutor.submit(() -> {
                    try {
                        log.info("partAssociations is ->" + partAssociations.stream().collect(Collectors.joining(",")));
                        String command = String.format("%s %s %s %s %s %s","sbatch","--wait", config.getScript(),Math.abs(partAsscnIds.hashCode()),partAssociations.stream().collect(Collectors.joining(",")), executorPool);
                        log.info("COmmand is"+command);
                        Process process = Runtime.getRuntime().exec(command);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String str = "";
                        while((str = bufferedReader.readLine()) != null){
                            log.info("Process returned this message"+str);
                            bsubLog.info("Process returned this message"+str);
                        }
                        int exitCode = process.waitFor();

                        log.info("Exit code for the Process"+exitCode);
                        bsubLog.info("Exit code for the Process"+exitCode);
                    }catch(IOException ex){
                        log.error("IOexception in executing shell command"+ex.getMessage(),ex);
                    }catch(InterruptedException ex){
                        log.error("InterruptedException om executing shell command"+ex.getMessage(),ex);
                    }
                });
            }
        }

        for(Future<?> future : futures){
            try {
                future.get();
            }catch (ExecutionException ex) {
                log.error("Exception in mapping submitter pipeline"+ex.getMessage(),ex);
                poolExecutor.shutdownNow();  // Stop accepting new tasks
                throw new SlurmProcessException("Nextflow returned error status");
                //break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        poolExecutor.shutdown();
        while(!poolExecutor.isTerminated()){
        }
        Date endDate = new Date();
        bsubLog.info("Bsub for Mapping Pipeline ended at {}",dateFormat.format(endDate));

    }


}

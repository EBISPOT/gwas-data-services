package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.Config;
import uk.ac.ebi.spot.gwas.service.MappingJobSubmitterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class MappingJobSubmitterServiceImpl implements MappingJobSubmitterService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");
    //private LsfSubCommand subCmd = new LsfSubCommand();

    private final Integer partitionSize = 2;

    @Autowired
    Config config;

    public void executePipeline(List<Long> asscnIds, String outDir, String errorDir, String executorPool)  {
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadPool());
        String activeProfile = config.getActiveProfile();
        log.info("Active profile is ->"+activeProfile);
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat(   "yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        bsubLog.info("Bsub for Mapping Pipeline started at {}",dateFormat.format(date));
          for(List<Long> partAsscnIds : ListUtils.partition(asscnIds, config.getPartitionSize())) {

              List<String> partAssociations = partAsscnIds.stream().map(String::valueOf).collect(Collectors.toList());
                if(activeProfile.equals("cluster")) {

                    poolExecutor.submit(() -> {
                        try {
                            log.info("partAssociations is ->" + partAssociations.stream().collect(Collectors.joining(",")));
                            String slurmOutputFile = String.format("%s %s/%s/%s/%s", "-o", config.getSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()),"output.log");
                            String slurmErrFile = String.format("%s %s/%s/%s/%s", "-e", config.getSlurmLogsLocation(), executorPool, Math.abs(partAsscnIds.hashCode()),"error.log");
                            String command = String.format("%s %s %s %s %s %s %s %s", "sbatch", slurmOutputFile, slurmErrFile, "--wait",config.getScript(),Math.abs(partAsscnIds.hashCode()),partAssociations.stream().collect(Collectors.joining(",")), executorPool);
                            log.info("COmmand is ->"+command);
                            Process process = Runtime.getRuntime().exec(command);
                            String str = "";
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((str = bufferedReader.readLine()) != null){
                                log.info("Process returned this message"+str);
                                bsubLog.info("Process returned this message"+str);
                            }
                            int exitCode = process.waitFor();
                            log.info("Exit code for the Process"+exitCode);
                            bsubLog.info("Exit code for the Process"+exitCode);
                        } catch (Exception ex) {
                            log.error("Io exception" + ex.getMessage(), ex);
                        }
                    });
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

        poolExecutor.shutdown();
        while(!poolExecutor.isTerminated()){
        }
        Date endDate = new Date();
        bsubLog.info("Bsub for Mapping Pipeline ended at {}",dateFormat.format(endDate));

    }


}

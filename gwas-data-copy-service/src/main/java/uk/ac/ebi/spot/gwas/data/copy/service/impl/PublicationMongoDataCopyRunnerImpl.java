package uk.ac.ebi.spot.gwas.data.copy.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.data.copy.config.Config;
import uk.ac.ebi.spot.gwas.data.copy.service.PublicationMongoDataCopyRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class PublicationMongoDataCopyRunnerImpl implements PublicationMongoDataCopyRunner {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    Config config;

    public void executeRunner(List<Long> ids, String errorDir, String outputDir, String executorPool) {

        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadPool());
        String activeProfile = config.getActiveProfile();
        log.info("Active profile is ->" + activeProfile);
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        bsubLog.info("Bsub for Data Copy started at {}", dateFormat.format(date));
        for (List<Long> partIds : ListUtils.partition(ids, config.getPartitionSize())) {

            List<String> partIdsJoined = partIds.stream().map(String::valueOf).collect(Collectors.toList());
            poolExecutor.submit(() -> {
                try {
                    log.info("partPublications is ->" + partIdsJoined.stream().collect(Collectors.joining(",")));
                    String slurmOutputFile = String.format("%s %s/%s/%s/%s", "-o", config.getSlurmLogsLocation(), executorPool, Math.abs(partIds.hashCode()), "output.log");
                    String slurmErrFile = String.format("%s %s/%s/%s/%s", "-e", config.getSlurmLogsLocation(), executorPool, Math.abs(partIds.hashCode()), "error.log");
                    String command = String.format("%s %s %s %s %s %s %s %s", "sbatch", slurmOutputFile, slurmErrFile, "--wait", config.getScript(), Math.abs(partIds.hashCode()), partIdsJoined.stream().collect(Collectors.joining(" ")), executorPool);
                    log.info("COmmand is ->" + command);
                    Process process = Runtime.getRuntime().exec(command);
                    String str = "";
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while ((str = bufferedReader.readLine()) != null) {
                        log.info("Process returned this message" + str);
                        bsubLog.info("Process returned this message" + str);
                    }
                    int exitCode = process.waitFor();
                    log.info("Exit code for the Process" + exitCode);
                    bsubLog.info("Exit code for the Process" + exitCode);
                } catch (Exception ex) {
                    log.error("Io exception" + ex.getMessage(), ex);
                }
            });

        }

        poolExecutor.shutdown();
        while (!poolExecutor.isTerminated()) {
        }
        Date endDate = new Date();
        bsubLog.info("Bsub for Data Copy ended at {}", dateFormat.format(endDate));

    }
}

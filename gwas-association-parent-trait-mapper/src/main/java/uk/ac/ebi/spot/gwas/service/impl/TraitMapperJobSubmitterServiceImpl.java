package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.config.Config;
import uk.ac.ebi.spot.gwas.service.TraitMapperJobSubmitterService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TraitMapperJobSubmitterServiceImpl implements TraitMapperJobSubmitterService {

    Config config;

    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    public TraitMapperJobSubmitterServiceImpl(Config config) {
        this.config = config;
    }

    @Transactional(readOnly = true)
    public void executePipeline(List<String> shortForms, String outDir, String errorDir, String executorPool, String parentEfoTerm) {
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadPool());
        String activeProfile = config.getActiveProfile();
        log.info("Active profile is ->" + activeProfile);
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        bsubLog.info("Bsub for Parent trait Mapper started at {}", dateFormat.format(new Date()));
        for (List<String> partshortForms : ListUtils.partition(shortForms, config.getPartitionSize())) {

            if (activeProfile.equals("cluster")) {
                poolExecutor.submit(() -> {
                    try {
                        log.info("partshortForms is ->" + partshortForms.stream().collect(Collectors.joining(",")));
                        String slurmOutputFile = String.format("%s %s/%s/%s/%s", "-o", config.getSlurmLogsLocation(), executorPool, Math.abs(partshortForms.hashCode()), "output.log");
                        String slurmErrFile = String.format("%s %s/%s/%s/%s", "-e", config.getSlurmLogsLocation(), executorPool, Math.abs(partshortForms.hashCode()), "error.log");
                        String command = String.format("%s %s %s %s %s %s %s %s %s", "sbatch", slurmOutputFile, slurmErrFile, "--wait", config.getScript(), Math.abs(partshortForms.hashCode())
                                , partshortForms.stream().collect(Collectors.joining(",")), executorPool, parentEfoTerm);
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
            } else {
                log.info("Active profile is ->" + activeProfile);
            }
        }
        poolExecutor.shutdown();
        while (!poolExecutor.isTerminated()) {
        }
        bsubLog.info("Bsub for Parent trait Mapper ended at {}", dateFormat.format(new Date()));
    }
}

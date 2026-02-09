package uk.ac.ebi.spot.gwas.submission.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class NextFlowJobConfig {

    @Value("${nextflow.script}")
    private String slurmJobScript;

    @Value("${slurm.logslocation}")
    private String slurmLogsLocation;

    @Value("${nextflow.command}")
    private String  nextflowJobCommand;

    @Value("${nextflow-import.db:#{NULL}}")
    private String dbName;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${email-config.to-address}")
    private String toAddress;

    @Value("${submission.partition.size}")
    private String partitionSize;

    @Value("${nextflow.job.retries}")
    private Integer retries;

    public String getDbName() {
        return dbName;
    }

}



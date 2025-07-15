package uk.ac.ebi.spot.gwas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${script}")
    private String script;

    @Value("${gene-mapper.script}")
    private String geneMapperScript;

    @Value("${executor.thread-pool.count}")
    private Integer threadPool;

    @Value("${association.partition.size}")
    private Integer partitionSize;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${slurm.logslocation}")
    private String slurmLogsLocation;

    @Value("${exclude.efo.shortforms}")
    private String excludeLargeEfos;

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public Integer getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(Integer threadPool) {
        this.threadPool = threadPool;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    public String getGeneMapperScript() {
        return geneMapperScript;
    }

    public void setGeneMapperScript(String geneMapperScript) {
        this.geneMapperScript = geneMapperScript;
    }

    public String getSlurmLogsLocation() {
        return slurmLogsLocation;
    }


    public String getExcludeLargeEfos() {
        return excludeLargeEfos;
    }
}



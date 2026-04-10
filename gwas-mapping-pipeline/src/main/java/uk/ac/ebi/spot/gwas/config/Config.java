package uk.ac.ebi.spot.gwas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.constants.GeneralCommon;


@Component
public class Config {

    @Value("${script}")
    private String script;

    @Value("${pmid.script}")
    private String pmidScript;

    @Value("${executor.thread-pool.count}")
    private Integer threadPool;

    @Value("${association.partition.size}")
    private Integer partitionSize;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${slurm.logslocation}")
    private String slurmLogsLocation;

    @Value("${slurm.pmid.logslocation}")
    private String pmidSlurmLogsLocation;

    @Value("${mapping-pipeline.mongo.db:#{NULL}}")
    private String dbName;

    @Value("${spring.data.mongodb.uri:#{NULL}}")
    private String mongoUri;





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

    public String getSlurmLogsLocation() {
        return slurmLogsLocation;
    }

    public String getPmidScript() {
        return pmidScript;
    }

    public void setPmidScript(String pmidScript) {
        this.pmidScript = pmidScript;
    }

    public String getPmidSlurmLogsLocation() {
        return pmidSlurmLogsLocation;
    }

    public String getDbName() {
        return dbName;
    }


    public String getMongoUri() {
        return mongoUri;
    }

    public String getDbUser() {
        return System.getenv(GeneralCommon.DB_USER);
    }

    public String getDbPassword() {
        return System.getenv(GeneralCommon.DB_PASSWORD);
    }


}



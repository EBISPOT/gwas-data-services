package uk.ac.ebi.spot.gwas.submission.nextflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NextFlowJobConfig {

    @Value("${nextflow-import.db:#{NULL}}")
    private String dbName;

    public String getDbName() {
        return dbName;
    }
}

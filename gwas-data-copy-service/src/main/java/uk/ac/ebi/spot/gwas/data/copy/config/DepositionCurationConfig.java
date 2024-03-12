package uk.ac.ebi.spot.gwas.data.copy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class DepositionCurationConfig {



    @Value("${gwas-curation.db:#{NULL}}")
    private String dbName;


    public String getDbName() {
        return dbName;
    }


}

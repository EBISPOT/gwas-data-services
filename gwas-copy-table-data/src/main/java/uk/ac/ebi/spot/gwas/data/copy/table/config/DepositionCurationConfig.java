package uk.ac.ebi.spot.gwas.data.copy.table.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DepositionCurationConfig {



    @Value("${gwas-curation.db:#{NULL}}")
    private String dbName;

    private Map<String, String> curationStatusMap = new HashMap<>();


    public String getDbName() {
        return dbName;
    }

    public Map<String, String> getCurationStatusMap() {
        curationStatusMap.put("Level 1 ancestry done","Publish Study");
        curationStatusMap.put("Level 1 curation done","Submission complete");
        curationStatusMap.put("Level 2 curation done","Level 2 curation done");
        curationStatusMap.put("Publish study","Publish Study");
        curationStatusMap.put("Awaiting Curation","Awaiting submission");
        curationStatusMap.put("Outstanding Query","Pending curation query");
        curationStatusMap.put("CNV Paper","Permanently unpublished from Catalog");
        curationStatusMap.put("Curation Abandoned","Curation abandoned");
        curationStatusMap.put("Unpublished from catalog","Unpublished from Catalog");
        curationStatusMap.put("Pending author query","Pending author query");
        curationStatusMap.put("Awaiting EFO assignment","Awaiting EFO");
        curationStatusMap.put("Preliminary review done","Preliminary review done");
        curationStatusMap.put("Awaiting mapping","Awaiting mapping");
        curationStatusMap.put("Awaiting literature","Awaiting literature");
        curationStatusMap.put("Permanently unpublished from catalog","Permanently unpublished from Catalog");
        curationStatusMap.put("Scientific pilot","Scientific pilot");

        return curationStatusMap;
    }
}

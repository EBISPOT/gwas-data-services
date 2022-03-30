package uk.ac.ebi.spot.gwas.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppConfig {


    @Value("${ensembl.server}")
    private String server;

    @Value("${mapping.genomic_distance}")
    private int genomicDistance; // 100kb

    @Value("${mapping.ensembl_source}")
    private String ensemblSource;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.ncbi_logic_name}")
    private String ncbiLogicName;

    @Value("${mapping.ncbi_db_type}")
    private String ncbiDbType;

    @Value("${mapping.method}")
    private String mappingMethod;

    @Value("${mapping.version}")
    private String eRelease;

    @Value("${mapping.cache}/${mapping.version}/")
    private String cacheDir;

}

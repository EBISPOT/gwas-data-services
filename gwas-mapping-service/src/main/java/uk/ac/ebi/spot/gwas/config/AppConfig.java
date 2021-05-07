package uk.ac.ebi.spot.gwas.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppConfig {

    @Value("${mapping.genomic_distance}")
    private int genomicDistance; // 100kb

    @Value("${mapping.ensembl_source}")
    private String ensemblSource;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.method}")
    private String mappingMethod;

    @Value("${mapping.cache}/${mapping.version}/")
    private String cacheDir;

}

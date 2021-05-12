package uk.ac.ebi.spot.gwas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingUtil {

    private static final Logger log = LoggerFactory.getLogger(MappingUtil.class);

    private MappingUtil() {
        // Hide implicit public constructor
    }

    public static void statusLog(String dataType, int count, int total) {
        if (count % 20 == 0 || total - count < 20) {
            log.info("Got {} {} data out of {}", count, dataType,  total);
        }
    }


}



// getAllChromosomesAndPositions
// Ensembl Overlapping Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/1:67262335-67262335?feature=gene

// getAllChromosomes
// Ensembl chromosomeEnd [https://rest.ensembl.org/info/assembly/homo_sapiens/{chromosome} -> getlength (chrEnd)]

// Ensembl Upstream Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/{chromosome}:{pos_up}-{position}?feature=gene]

// Ensembl Downstream Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/{chromosome}:{position}-{pos_down}?feature=gene]

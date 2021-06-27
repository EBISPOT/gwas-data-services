package uk.ac.ebi.spot.gwas.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.dto.EnsemblData;
import uk.ac.ebi.spot.gwas.dto.Mapping;
import uk.ac.ebi.spot.gwas.dto.OverlapRegion;
import uk.ac.ebi.spot.gwas.dto.Variation;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.model.Region;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Service
public class Mapper {


    @Value("${mapping.genomic_distance}")
    private int genomicDistance; // 100kb

    @Value("${mapping.ensembl_source}")
    private String ensemblSource;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.method}")
    private String mappingMethod;

    private EnsemblData ensemblData;

    private String eRelease;

    public Collection<Location> getMappings(Variation variant) {
        Map<String, List<OverlapRegion>> cytoGeneticBand = ensemblData.getCytoGeneticBand();
        Collection<Location> locations = new ArrayList<>();
        List<Mapping> mappings = variant.getMappings();
        for (Mapping mapping : mappings) {
            String chromosome = mapping.getSeqRegionName();
            Integer position = mapping.getStart();

            if (Optional.ofNullable(chromosome).isPresent()) {
                String chrLocation = String.format("%s:%s-%s", chromosome, position, position);
                List<OverlapRegion> overlapRegions = cytoGeneticBand.get(chrLocation);

                Region region = new Region();
                if (!overlapRegions.isEmpty() && !Optional.ofNullable(overlapRegions.get(0).getError()).isPresent()) {
                    String cytogeneticBand = overlapRegions.get(0).getId();
                    Matcher matcher1 = Pattern.compile("^[0-9]+|[XY]$").matcher(chromosome); // Chromosomes
                    Matcher matcher2 = Pattern.compile("^MT$").matcher(chromosome);          // Mitochondria
                    if (matcher1.matches() || matcher2.matches()) {
                        region.setName(chromosome + cytogeneticBand);
                    }
                }
                Location location = new Location(chromosome, position, region);
                locations.add(location);
            }
        }
        return locations;
    }
}

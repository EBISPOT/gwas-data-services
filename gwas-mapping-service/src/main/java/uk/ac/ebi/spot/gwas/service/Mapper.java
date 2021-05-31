package uk.ac.ebi.spot.gwas.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.dto.*;
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


    private List<OverlapGene> getNearestGene(String chromosome,
                                             Integer snpPosition,
                                             Integer position,
                                             int boundary,
                                             String type, String source,
                                             EnsemblMappingResult mappingResult) {
        int position1 = position;
        int position2 = position;
        int snpPos = snpPosition;
        int newPos = position1;

        List<OverlapGene> closestGene = new ArrayList<>();
        int closestDistance = 0;
        if (type.equals("upstream")) {
            position1 = position2 - genomicDistance;
            position1 = (position1 < 0) ? boundary : position1;
            newPos = position1;
        } else {
            if (type.equals("downstream")) {
                position2 = position1 + genomicDistance;
                position2 = Math.min(position2, boundary);
                newPos = position2;
            }
        }

        Map<String, List<OverlapGene>> overlapGeneData =
                (source.equals(ncbiSource) ? ensemblData.getNcbiOverlapGene() : ensemblData.getEnsemblOverlapGene());

        String location = String.format("%s:%s-%s", chromosome, position1, position2);
        List<OverlapGene> overlapGenes = overlapGeneData.get(location);

        boolean geneError = false;
        if (overlapGenes != null && !overlapGenes.isEmpty()) {
            if (Optional.ofNullable(overlapGenes.get(0).getError()).isPresent()) {
                geneError = true;
            } else {
                for (OverlapGene overlapGene : overlapGenes) {
                    String geneName = overlapGene.getExternalName();

                    // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                    if (source.equals(ncbiSource)) {
                        if (geneName == null || mappingResult.getNcbiOverlappingGene().contains(geneName)) {
                            continue;
                        }
                    } else {
                        if (geneName == null || mappingResult.getEnsemblOverlappingGene().contains(geneName)) {
                            continue;
                        }
                    }

                    int distance = 0;
                    if (type.equals("upstream")) {
                        distance = snpPos - overlapGene.getEnd();
                    } else if (type.equals("downstream")) {
                        distance = overlapGene.getStart() - snpPos;
                    }

                    if ((distance < closestDistance && distance > 0) || closestDistance == 0) {
                        closestGene = Collections.singletonList(overlapGene);
                        closestDistance = distance;
                    }
                }
                // Recursive code to find the nearest upstream or downstream gene
                if (closestGene.isEmpty() && newPos != boundary) {
                    closestGene = this.getNearestGene(chromosome, snpPosition, newPos, boundary, type, source, mappingResult);
                }
            }
        } else {
            // Recursive code to find the nearest upstream or downstream gene
            if (newPos != boundary) {
                closestGene = this.getNearestGene(chromosome, snpPosition, newPos, boundary, type, source, mappingResult);
            }
        }

        return closestGene;
    }
}

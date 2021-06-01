package uk.ac.ebi.spot.gwas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.gwas.dto.AssemblyInfo;
import uk.ac.ebi.spot.gwas.dto.Mapping;
import uk.ac.ebi.spot.gwas.dto.Variation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static List<String> getAllChromosomesAndPositions(List<Variation> variants) {
        List<String> locations = new ArrayList<>();
        variants.forEach(variant -> locations.addAll(variant.getMappings().stream()
                                                             .map(mapping -> String.format("%s:%s-%s",
                                                                                           mapping.getSeqRegionName(),
                                                                                           mapping.getStart(),
                                                                                           mapping.getStart()))
                                                             .collect(Collectors.toList())));
        return locations.stream().map(String::trim).distinct().collect(Collectors.toList());
    }

    public static List<String> getAllChromosomes(List<Variation> variants) {
        List<String> locations = new ArrayList<>();
        variants.forEach(variant -> locations.addAll(variant.getMappings().stream()
                                                             .map(Mapping::getSeqRegionName)
                                                             .collect(Collectors.toList())));
        return locations.stream().map(String::trim).distinct().collect(Collectors.toList());
    }

    public static List<String> getUpstreamLocations(List<Variation> variants, int genomicDistance) {
        int chromStart = 1;
        List<String> locations = new ArrayList<>();
        variants.forEach(variant -> variant.getMappings().forEach(mapping -> {

            String chromosome = mapping.getSeqRegionName();
            int position = mapping.getStart();
            int positionUp = ((position - genomicDistance) < 0) ? chromStart : position - genomicDistance;
            locations.add(String.format("%s:%s-%s", chromosome, positionUp, position));
        }));
        return locations.stream().map(String::trim).distinct().collect(Collectors.toList());
    }

    public static List<String> getDownstreamLocations(List<Variation> variants, Map<String, AssemblyInfo> assemblyInfoMap, int genomicDistance) {
        List<String> locations = new ArrayList<>();
        variants.forEach(variant -> variant.getMappings().forEach(mapping -> {
            String chromosome = mapping.getSeqRegionName();
            int position = mapping.getStart();

            int chrEnd = assemblyInfoMap.get(chromosome).getLength();
            if (chrEnd != 0) {
                int positionDown = position + genomicDistance;
                positionDown = Math.min(positionDown, chrEnd);
                locations.add(String.format("%s:%s-%s", chromosome, position, positionDown));
            }
        }));
        return locations.stream().map(String::trim).distinct().collect(Collectors.toList());
    }

    // Using regular expression to parse description:
    public static String parseNCBIid( String description, String geneName){
        Pattern refseqIdPattern = Pattern.compile("Acc:(\\d+)]");
        Matcher matcher = refseqIdPattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.info("[Warning] NCBI ID for {} Was not found. ", geneName);
            return "NCBI ID was not found for this gene.";
        }
    }

    public static List<String> removeBlackListedVariants(List<String> snpRsIds){
        snpRsIds.removeIf(s -> s.contains("chr"));
        snpRsIds.removeIf(s -> s.contains("exm"));
        snpRsIds.removeIf(s -> s.contains("drb"));
        return snpRsIds;
    }


}



// getAllChromosomesAndPositions
// Ensembl Overlapping Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/1:67262335-67262335?feature=gene

// getAllChromosomes
// Ensembl chromosomeEnd [https://rest.ensembl.org/info/assembly/homo_sapiens/{chromosome} -> getlength (chrEnd)]

// Ensembl Upstream Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/{chromosome}:{pos_up}-{position}?feature=gene]

// Ensembl Downstream Genes [https://rest.ensembl.org/overlap/region/homo_sapiens/{chromosome}:{position}-{pos_down}?feature=gene]

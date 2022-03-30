package uk.ac.ebi.spot.gwas.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfo;
import uk.ac.ebi.spot.gwas.common.constant.DataType;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegion;
import uk.ac.ebi.spot.gwas.variation.Variation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CacheUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Date uniq = new Date();

    private CacheUtil() {
        // Hide implicit public constructor
    }

    public static Map<String, Variation> variation(DataType dataType, String cacheDir) {
        Map<String, Variation> variationMap = new HashMap<>();
        String cache = cacheDir + dataType.getFileLocation();

        if (Files.exists(Paths.get(cache))) {
            variationMap = mapper.convertValue(readJsonLocal(cache), new TypeReference<Map<String, Variation>>() {
            });
        }
        return variationMap;
    }

    public static Map<String, GeneSymbol> reportedGenes(DataType dataType, String cacheDir) {
        Map<String, GeneSymbol> geneSymbolMap = new HashMap<>();
        String cache = cacheDir + dataType.getFileLocation();

        if (Files.exists(Paths.get(cache))) {
            geneSymbolMap = mapper.convertValue(readJsonLocal(cache), new TypeReference<Map<String, GeneSymbol>>() {
            });
        }
        return geneSymbolMap;
    }


    public static Map<String, List<OverlapRegion>> cytoGeneticBand(DataType dataType, String cacheDir) {
        Map<String, List<OverlapRegion>> cytoGeneticBand = new HashMap<>();
        String cache = cacheDir + dataType.getFileLocation();
        if (Files.exists(Paths.get(cache))) {
            cytoGeneticBand = mapper.convertValue(readJsonLocal(cache), new TypeReference<Map<String, List<OverlapRegion>>>() {});
        }
        return cytoGeneticBand;
    }

    public static Map<String, AssemblyInfo> assemblyInfo(DataType dataType, String cacheDir) {

        Map<String, AssemblyInfo> assemblyInfos = new HashMap<>();
        String cache = cacheDir + dataType.getFileLocation();
        if (Files.exists(Paths.get(cache))) {
            assemblyInfos = mapper.convertValue(readJsonLocal(cache), new TypeReference<Map<String, AssemblyInfo>>() {
            });
        }
        return assemblyInfos;
    }


    public static Map<String, List<OverlapGene>> overlappingGenes(DataType dataType, String cacheDir) {

        Map<String, List<OverlapGene>> overlappingGenes = new HashMap<>();
        String cache = cacheDir + dataType.getFileLocation();
        if (Files.exists(Paths.get(cache))) {
            overlappingGenes = mapper.convertValue(readJsonLocal(cache), new TypeReference<Map<String, List<OverlapGene>>>() {
            });
        }
        return overlappingGenes;
    }

    public static void saveToFile(DataType dataType, String cacheDir, Object dataToSave) {
        String fileName = cacheDir + dataType.getFileLocation();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String backup = cacheDir + String.format("backup-%s/%s", dateFormat.format(uniq), dataType.getFileLocation());
        try {
            if (Files.exists(Paths.get(backup))) {
                Files.delete(Paths.get(backup));
            }
            if (Files.exists(Paths.get(fileName))) {
                FileUtils.moveFile(FileUtils.getFile(fileName), FileUtils.getFile(backup));
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
                writer.append(mapper.writeValueAsString(dataToSave));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode readJsonLocal(String jsonFileLink) {
        JsonNode jsonNode = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonFileLink));
            jsonNode = mapper.readTree(br);
        } catch (Exception e) {
            log.info("Could not read json file");
        }
        return jsonNode;
    }
}

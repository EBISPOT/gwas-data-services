package uk.ac.ebi.spot.gwas.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.Variation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CacheUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

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

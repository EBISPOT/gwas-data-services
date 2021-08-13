package uk.ac.ebi.spot.gwas.service.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.constant.Uri;
import uk.ac.ebi.spot.gwas.dto.AssemblyInfo;
import uk.ac.ebi.spot.gwas.service.data.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.service.mapping.MappingApiService;
import uk.ac.ebi.spot.gwas.util.CacheUtil;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AssemblyInfoService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final MappingApiService mappingApiService;

    public AssemblyInfoService(AppConfig config,
                               EnsemblRestcallHistoryService historyService,
                               MappingApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
    }

    public Map<String, AssemblyInfo> getAssemblyInfo(DataType dataType, List<String> chromosomes) throws InterruptedException { // Get Chromosome End
        int count = 1;
        Map<String, AssemblyInfo> cached = CacheUtil.assemblyInfo(dataType, config.getCacheDir());
        for (String chromosome : chromosomes) {
            AssemblyInfo assemblyInfo = cached.get(chromosome);
            if (assemblyInfo == null) {
                cached.putAll(this.apiCall(chromosome));
            }
            MappingUtil.statusLog(dataType.name(), count++, chromosomes.size());
        }
        CacheUtil.saveToFile(dataType, config.getCacheDir(), cached);
        return cached;
    }

    // fix manip here
    public Map<String, AssemblyInfo> apiCall(String chromosome) throws InterruptedException { // chromosomeEnd
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.INFO_ASSEMBLY, chromosome);
        Map<String, AssemblyInfo> assemblyInfoMap = new HashMap<>();

        AssemblyInfo assemblyInfo = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), AssemblyInfo.class))
                .orElseGet(AssemblyInfo::new);
        assemblyInfoMap.put(chromosome, assemblyInfo);
        return assemblyInfoMap;
    }

}

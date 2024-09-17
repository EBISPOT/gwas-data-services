package uk.ac.ebi.spot.gwas.assembly_info;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.DataType;
import uk.ac.ebi.spot.gwas.common.constant.Type;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.common.service.RestResponseResultBuilderService;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.mapping.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.common.service.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.common.service.ApiService;
import uk.ac.ebi.spot.gwas.common.util.CacheUtil;
import uk.ac.ebi.spot.gwas.common.util.MappingUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AssemblyInfoService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;
    RestResponseResultBuilderService restResponseResultBuilderService;

    public AssemblyInfoService(AppConfig config,
                               EnsemblRestcallHistoryService historyService,
                               ApiService mappingApiService,
                               RestResponseResultBuilderService restResponseResultBuilderService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
        this.restResponseResultBuilderService = restResponseResultBuilderService;
    }

    public Map<String, AssemblyInfo> getAssemblyInfo(DataType dataType, List<String> chromosomes) { // Get Chromosome End
        int count = 1;
        Map<String, AssemblyInfo> cached = CacheUtil.assemblyInfo(dataType, config.getCacheDir());
        for (String chromosome : chromosomes) {
            AssemblyInfo assemblyInfo = cached.get(chromosome);
            if (assemblyInfo == null) {
                cached.putAll(this.restApiCall(chromosome));
            }
            MappingUtil.statusLog(dataType.name(), count++, chromosomes.size());
        }
        CacheUtil.saveToFile(dataType, config.getCacheDir(), cached);
        return cached;
    }

    @Cacheable(value = "assemblyInfo")
    public AssemblyInfo getAssemblyInfoFromDB(String chromosome) {
        log.warn("Retrieving Assembly infor for chromosome: {}", chromosome);
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.INFO_ASSEMBLY, chromosome, config.getERelease());
        AssemblyInfo assemblyInfo = new AssemblyInfo();
        if (result == null) {
            assemblyInfo = this.restApiCall(chromosome).get(chromosome);
        } else {
            try {
                log.info("inside getting assemblyInfo from History block");
                assemblyInfo = mapper.readValue(result.getRestResult(), AssemblyInfo.class);
            } catch (JsonProcessingException e) { log.info(e.getMessage()); }
        }
        return assemblyInfo;
    }

    // fix manip here
    public Map<String, AssemblyInfo> restApiCall(String chromosome) { // chromosomeEnd
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.INFO_ASSEMBLY, chromosome);
        log.debug("AssemblyInfo url is {}", uri);
        Map<String, AssemblyInfo> assemblyInfoMap = new HashMap<>();
        Optional<ResponseEntity<AssemblyInfo>> optionalEntity = mappingApiService.getRequestAssemblyInfo(uri);
        AssemblyInfo assemblyInfo = optionalEntity
                .map(response -> mapper.convertValue(response.getBody(), AssemblyInfo.class))
                .orElseGet(AssemblyInfo::new);
        assemblyInfoMap.put(chromosome, assemblyInfo);
        String assemblyInfoResponse = "";
        try {

            assemblyInfoResponse =mapper.writeValueAsString(assemblyInfo);
        } catch(Exception ex) {
            log.error("Exception in writing object as string in AssemblyInfoService"+ex.getMessage(),ex);
        }

        restResponseResultBuilderService.buildResponseResult(uri, chromosome, Type.INFO_ASSEMBLY, optionalEntity.get(),assemblyInfoResponse);

        return assemblyInfoMap;
    }

}

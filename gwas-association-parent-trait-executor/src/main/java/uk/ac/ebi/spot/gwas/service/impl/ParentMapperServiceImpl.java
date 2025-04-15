package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.Config;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.EFOTraitRepository;
import uk.ac.ebi.spot.gwas.service.ParentMapperService;
import uk.ac.ebi.spot.gwas.service.TraitMapperJobSubmitterService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentMapperServiceImpl implements ParentMapperService {


    EFOTraitRepository efoTraitRepository;

    TraitMapperJobSubmitterService traitMapperJobSubmitterService;

    Config config;

    @Autowired
    public ParentMapperServiceImpl(EFOTraitRepository efoTraitRepository,
                                   TraitMapperJobSubmitterService traitMapperJobSubmitterService,
                                   Config config) {
        this.efoTraitRepository = efoTraitRepository;
        this.traitMapperJobSubmitterService = traitMapperJobSubmitterService;
        this.config = config;
    }


    public void executeFileBasedParentMapper(String outputDir, String inputDir, List<String> efoShortForms, String executionMode) {
        int count = 0;
        for (List<String> partshortForms : ListUtils.partition(efoShortForms, 1000)) {
            traitMapperJobSubmitterService.executePipeline(partshortForms, outputDir, inputDir, "executor-"+count, executionMode);
            count++;
        }
    }

    public void executeLargeEFOParentMapper(String outputDir, String inputDir, String executionMode) {
        for(List<String> partshortForms : ListUtils.partition(Arrays.asList(config.getExcludeLargeEfos().split(",")), 3))
        {
            traitMapperJobSubmitterService.executePipeline(partshortForms, outputDir, inputDir, "executor-1", executionMode);
        }
    }

    public void executeParentMapper(String outputDir, String inputDir, String executionMode) {
        Long count = efoTraitRepository.count();
        List<String> excludeShortForms = Arrays.asList(config.getExcludeLargeEfos().split(","));
        long bucket = count / 1000;
        for(int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i,1000);
            List<String> efoShortForms = efoTraitRepository.findAll(pageable)
                    .stream()
                    .map(EfoTrait::getShortForm)
                    .filter( shorForm -> !excludeShortForms.contains(shorForm))
                    .collect(Collectors.toList());
            traitMapperJobSubmitterService.executePipeline(efoShortForms, outputDir, inputDir, "executor-"+i, executionMode);
        }
    }

}

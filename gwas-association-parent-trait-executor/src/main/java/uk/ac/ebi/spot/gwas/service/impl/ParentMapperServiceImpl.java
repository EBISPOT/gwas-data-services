package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.EFOTraitRepository;
import uk.ac.ebi.spot.gwas.service.ParentMapperService;
import uk.ac.ebi.spot.gwas.service.TraitMapperJobSubmitterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentMapperServiceImpl implements ParentMapperService {


    EFOTraitRepository efoTraitRepository;

    TraitMapperJobSubmitterService traitMapperJobSubmitterService;

    @Autowired
    public ParentMapperServiceImpl(EFOTraitRepository efoTraitRepository,
                                   TraitMapperJobSubmitterService traitMapperJobSubmitterService) {
        this.efoTraitRepository = efoTraitRepository;
        this.traitMapperJobSubmitterService = traitMapperJobSubmitterService;
    }

    public void executeParentMapper(String outputDir, String inputDir) {
        Long count = efoTraitRepository.count();
        long bucket = count / 1000;
        for(int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i,1000);
            List<String> efoShortForms = efoTraitRepository.findAll(pageable)
                    .stream()
                    .map(EfoTrait::getShortForm)
                    .collect(Collectors.toList());
            traitMapperJobSubmitterService.executePipeline(efoShortForms, outputDir, inputDir, "executor-"+i);
        }
    }

}

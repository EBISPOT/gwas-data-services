package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.service.AssociationGeneMapperService;
import uk.ac.ebi.spot.gwas.service.TraitMapperJobSubmitterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociationGeneMapperServiceImpl implements AssociationGeneMapperService {

    AssociationRepository associationRepository;

    TraitMapperJobSubmitterService traitMapperJobSubmitterService;

    @Autowired
    public AssociationGeneMapperServiceImpl(AssociationRepository associationRepository,
                                            TraitMapperJobSubmitterService traitMapperJobSubmitterService) {
        this.associationRepository = associationRepository;
        this.traitMapperJobSubmitterService = traitMapperJobSubmitterService;
    }

    public void mapGenes(String outputDir, String inputDir, String executionMode) {
        Long count = associationRepository.count();
        long bucket = count / 1000;
        for (int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            List<String> asscnIds = associationRepository.findAll(pageable)
                    .stream()
                    .map(Association::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            traitMapperJobSubmitterService.executePipeline(asscnIds, outputDir, inputDir, "executor-" + i, executionMode);
        }
    }


}
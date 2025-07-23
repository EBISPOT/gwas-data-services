package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.SnpGeneMapperService;
import uk.ac.ebi.spot.gwas.service.TraitMapperJobSubmitterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnpGeneMapperServiceImpl implements SnpGeneMapperService {

    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    TraitMapperJobSubmitterService traitMapperJobSubmitterService;


    @Autowired
    public SnpGeneMapperServiceImpl(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                    TraitMapperJobSubmitterService traitMapperJobSubmitterService) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.traitMapperJobSubmitterService = traitMapperJobSubmitterService;
    }

    public void mapGenes(String outputDir, String inputDir, String executionMode) {
        Long count = singleNucleotidePolymorphismRepository.count();
        long bucket = count / 1000;
        for (int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            List<String> asscnIds = singleNucleotidePolymorphismRepository.findAll(pageable)
                    .stream()
                    .map(SingleNucleotidePolymorphism::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            traitMapperJobSubmitterService.executePipeline(asscnIds, outputDir, inputDir, "executor-" + i, executionMode);
        }
    }
}

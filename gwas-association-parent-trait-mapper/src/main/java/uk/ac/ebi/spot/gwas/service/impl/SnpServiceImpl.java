package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;
import uk.ac.ebi.spot.gwas.service.GeneMappingService;
import uk.ac.ebi.spot.gwas.service.SnpRetrieveService;
import uk.ac.ebi.spot.gwas.service.SnpService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SnpServiceImpl implements SnpService {

    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    SnpRetrieveService snpRetrieveService;

    GeneMappingService geneMappingService;

    @Autowired
    public SnpServiceImpl(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                          SnpRetrieveService snpRetrieveService,
                          GeneMappingService geneMappingService) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.snpRetrieveService = snpRetrieveService;
        this.geneMappingService = geneMappingService;
    }

    @Transactional
    public void updateSnpMappingGenes(List<Long> snpIds) {
        for(Long snpId : snpIds) {
            SingleNucleotidePolymorphism singleNucleotidePolymorphism = snpRetrieveService.getSnp(snpId);
            if(singleNucleotidePolymorphism != null) {
                List<SnpGeneProjection> snpGeneProjections = snpRetrieveService.findOverLappingGenes(snpId, "Ensembl");
                if(snpGeneProjections.isEmpty()) {
                    snpGeneProjections = snpRetrieveService.findUpDownStreamGenes(snpId, "Ensembl");
                }
                geneMappingService.updateSnpGeneMapping(snpGeneProjections);
            }
        }
    }
}

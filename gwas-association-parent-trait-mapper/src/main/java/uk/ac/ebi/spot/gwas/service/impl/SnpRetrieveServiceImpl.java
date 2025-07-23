package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;
import uk.ac.ebi.spot.gwas.service.SnpRetrieveService;

import java.util.List;

@Service
public class SnpRetrieveServiceImpl implements SnpRetrieveService {

    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public SnpRetrieveServiceImpl(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @Transactional(readOnly = true)
    public List<SnpGeneProjection> findOverLappingGenes(Long snpId , String source) {
        return singleNucleotidePolymorphismRepository.findOverLappingGenes(snpId, source);
    }

    @Transactional(readOnly = true)
    public List<SnpGeneProjection> findUpDownStreamGenes(Long snpId , String source) {
        return singleNucleotidePolymorphismRepository.findUpDownStreamGenes(snpId, source);
    }

    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism getSnp(Long snpId) {
        return singleNucleotidePolymorphismRepository.findById(snpId)
                .orElse(null);
    }


}

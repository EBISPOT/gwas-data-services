package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.SnpUpdateService;

import java.util.List;

@Service
public class SnpUpdateServiceImpl implements SnpUpdateService {

    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public SnpUpdateServiceImpl(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSnpMappedGene(Long snpId, List<Gene> mappedGenes) {
        singleNucleotidePolymorphismRepository.findById(snpId).ifPresent(snp -> {
            snp.setMappedSnpGenes(mappedGenes);
            singleNucleotidePolymorphismRepository.save(snp);
        });
    }
}

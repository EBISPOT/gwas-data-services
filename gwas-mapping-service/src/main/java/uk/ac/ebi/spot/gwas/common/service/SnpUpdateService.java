package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.model.Gene;
import uk.ac.ebi.spot.gwas.common.repository.SingleNucleotidePolymorphismRepository;

import java.util.List;

@Service
public class SnpUpdateService {

    @Autowired
    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;


    public void updateSnpMappedGene(Long snpId, List<Gene> mappedGenes) {
        singleNucleotidePolymorphismRepository.findById(snpId).ifPresent(snp -> {
            snp.setMappedSnpGenes(mappedGenes);
            singleNucleotidePolymorphismRepository.save(snp);
        });
    }
}

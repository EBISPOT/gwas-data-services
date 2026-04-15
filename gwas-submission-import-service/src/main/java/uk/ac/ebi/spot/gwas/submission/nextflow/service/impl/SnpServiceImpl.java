package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SnpService;

import java.util.Optional;

@Service
public class SnpServiceImpl implements SnpService {

    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    public SnpServiceImpl(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }


    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism getSnp(String rsId) {
        return singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(rsId).orElse(null);
    }

    public SingleNucleotidePolymorphism saveSnp(SingleNucleotidePolymorphism singleNucleotidePolymorphism) {
        //Optional<SingleNucleotidePolymorphism> optSnpInDB =  singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(singleNucleotidePolymorphism.getRsId());
        //return optSnpInDB.orElseGet(() -> singleNucleotidePolymorphismRepository.save(singleNucleotidePolymorphism));
        return singleNucleotidePolymorphismRepository.save(singleNucleotidePolymorphism);
    }
}

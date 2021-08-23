package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;

import java.util.Collection;

@Service
public class SingleNucleotidePolymorphismQueryService {

    @Autowired
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;


    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism findByRsIdIgnoreCase(String rsId) {
        SingleNucleotidePolymorphism singleNucleotidePolymorphism =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(rsId);
        loadAssociatedData(singleNucleotidePolymorphism);
        return singleNucleotidePolymorphism;
    }

    @Transactional(readOnly = true)
    public Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId) {
        Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus =
                singleNucleotidePolymorphismRepository.findByRiskAllelesLociId(locusId);
        snpsLinkedToLocus.forEach(this::loadAssociatedData);
        return snpsLinkedToLocus;
    }

    public void loadAssociatedData(SingleNucleotidePolymorphism snp) {

        if (snp.getLocations() != null) {
            snp.getLocations().size();
        }
        if (snp.getGenomicContexts() != null) {
            snp.getGenomicContexts().size();
        }
    }
}

package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.common.projection.SnpGeneProjection;
import uk.ac.ebi.spot.gwas.common.repository.SingleNucleotidePolymorphismRepository;

import java.util.Collection;
import java.util.List;

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

    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism getSnp(String rsId) {
        return singleNucleotidePolymorphismRepository.findByRsId(rsId);
    }


    @Transactional(readOnly = true)
    public List<SnpGeneProjection> findOverLappingGenes(Long snpId , String source) {
        return singleNucleotidePolymorphismRepository.findOverLappingGenes(snpId, source);
    }

    @Transactional(readOnly = true)
    public List<SnpGeneProjection> findUpDownStreamGenes(Long snpId , String source) {
        return singleNucleotidePolymorphismRepository.findUpDownStreamGenes(snpId, source);
    }
}

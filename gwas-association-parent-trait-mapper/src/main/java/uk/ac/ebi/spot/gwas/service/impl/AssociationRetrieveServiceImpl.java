package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;
import uk.ac.ebi.spot.gwas.service.AssociationRetrieveService;

import java.util.List;

@Service
public class AssociationRetrieveServiceImpl implements AssociationRetrieveService {

    AssociationRepository associationRepository;

    public AssociationRetrieveServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Transactional(readOnly = true)
    public List<AssociationGeneProjection> findOverLappingGenes(Long accsnId , String source, Long snpId) {

        return associationRepository.findOverLappingGenes(accsnId, source, snpId);
   }

    @Transactional(readOnly = true)
    public List<AssociationGeneProjection> findUpDownStreamGenes(Long accsnId , String source, Long snpId) {
        return associationRepository.findUpDownStreamGenes(accsnId, source, snpId);
    }

    @Transactional(readOnly = true)
    public  Association getAssociation(Long accsnId) {
        return associationRepository.findById(accsnId).orElse(null);
    }
}

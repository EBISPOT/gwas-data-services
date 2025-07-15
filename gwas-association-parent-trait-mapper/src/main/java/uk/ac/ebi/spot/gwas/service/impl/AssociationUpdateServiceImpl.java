package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.service.AssociationUpdateService;

import java.util.List;

@Service
public class AssociationUpdateServiceImpl implements AssociationUpdateService {

    AssociationRepository associationRepository;

    public AssociationUpdateServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAssociationMappedGene(Long associationId, List<Gene> mappedGenes) {
        associationRepository.findById(associationId).ifPresent(association -> {
            association.setMappedGenes(mappedGenes);
            associationRepository.save(association);
        });
    }
}


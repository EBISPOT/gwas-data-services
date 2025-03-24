package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.service.AssociationService;

@Slf4j
@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;


    @Autowired
    public AssociationServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }



    @Override
    @Transactional(readOnly = true)
    public Page<Association> findAssociationByShortForm(String shortForm, Pageable pageable) {
       return associationRepository.findByEfoTraitsShortForm(shortForm, pageable);
    }

}

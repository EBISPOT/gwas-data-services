package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.service.AssociationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;

    @Autowired
    public AssociationServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void loadAssociationsWithParentEfo(EfoTrait parenEfo) {
        for (EfoTrait childEfo : parenEfo.getParentChildEfoTraits()) {
            Long count = associationRepository.countAssociationsByEfoTraitsShortForm(childEfo.getShortForm());
            long bucket = (count / 1000);
            for (int i = 0; i <= bucket; i++) {
                Pageable pageable = PageRequest.of(i, 100);
                associationRepository.findByEfoTraitsShortForm(childEfo.getShortForm(), pageable).
                        forEach(association -> {
                            List<String> shortForms = association.getParentEfoTraits().stream()
                                    .map(EfoTrait::getShortForm)
                                    .collect(Collectors.toList());
                            if(!shortForms.contains(parenEfo.getShortForm())) {
                                association.getParentEfoTraits().add(parenEfo);
                            }
                            associationRepository.save(association);
                        });
            }
        }
    }
}

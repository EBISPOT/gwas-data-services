package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.Locus;
import uk.ac.ebi.spot.gwas.model.RiskAllele;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.AssociationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssociationServiceImpl implements AssociationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AssociationRepository associationRepository;
    @Autowired
    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    @Transactional(readOnly = true)
    public Set<Association> getAssociationBasedOnRsId(String rsId) {
        log.info("Rsid is ->"+rsId);
        List<Association> associations = singleNucleotidePolymorphismRepository.findAssociationsUsingRsId(rsId);
        Set<Association> associationSet = new HashSet<>(associations);
        associationSet.forEach(association -> log.info("Association Id ->"+association.getId()));
        return associationSet;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMappingDetails(List<Long> ids) {

       for(List<Long> partIds :  ListUtils.partition(ids, 100)) {
           List<Association> partAsscns = associationRepository.findAllById(partIds);
           partAsscns.forEach(association -> {
               association.setLastMappingDate(null);
               associationRepository.save(association);

           });
       }
    }



}

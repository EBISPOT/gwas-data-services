package uk.ac.ebi.spot.gwas.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.association.Association;
import uk.ac.ebi.spot.gwas.association.AssociationRepository;

import java.util.Date;

@Service
public class MappingRecordService {

    @Autowired
    private AssociationRepository associationRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void updateAssociationMappingRecord(Association association, Date mappingDate, String mappedBy) {
        association.setLastMappingDate(mappingDate);
        association.setLastMappingPerformedBy(mappedBy);
        associationRepository.save(association);
    }

}

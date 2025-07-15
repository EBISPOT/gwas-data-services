package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;
import uk.ac.ebi.spot.gwas.service.AssociationRetrieveService;
import uk.ac.ebi.spot.gwas.service.AssociationService;
import uk.ac.ebi.spot.gwas.service.GeneMappingService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;


    AssociationRetrieveService associationRetrieveService;

    GeneMappingService geneMappingService;


    @Autowired
    public AssociationServiceImpl(AssociationRepository associationRepository,
                                  AssociationRetrieveService associationRetrieveService,
                                  GeneMappingService geneMappingService) {
        this.associationRepository = associationRepository;
        this.associationRetrieveService = associationRetrieveService;
        this.geneMappingService = geneMappingService;
    }



    @Override
    @Transactional(readOnly = true)
    public Page<Association> findAssociationByShortForm(String shortForm, Pageable pageable) {
       return associationRepository.findByEfoTraitsShortForm(shortForm, pageable);
    }

    @Transactional
    public void updateAssociationMappingGenes(List<Long> associationIds) {
        for (Long accsnId : associationIds) {
            List<AssociationGeneProjection> finalAsscnGeneIds = new ArrayList<>();
            Association association = associationRetrieveService.getAssociation(accsnId);
            if (association != null) {
                association.getSnps().forEach(snp -> {
                List<AssociationGeneProjection> asscnGeneIds = associationRetrieveService.findOverLappingGenes(accsnId, "Ensembl", snp.getId());
                if (asscnGeneIds.isEmpty()) {
                    asscnGeneIds = associationRetrieveService.findUpDownStreamGenes(accsnId, "Ensembl", snp.getId());
                }
                    finalAsscnGeneIds.addAll(asscnGeneIds);
            });
                geneMappingService.updateGeneMapping(finalAsscnGeneIds);
        }
    }
    }

}

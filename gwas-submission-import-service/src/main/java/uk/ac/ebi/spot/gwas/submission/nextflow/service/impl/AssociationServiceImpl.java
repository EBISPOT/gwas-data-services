package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.model.AssociationExtension;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.AssociationMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AssociationExtensionRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AssociationAssemblyService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AssociationService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.LociAttributesService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SnpService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;

    AssociationMongoRepository associationMongoRepository;

    LociAttributesService lociAttributesService;

    AssociationAssemblyService associationAssemblyService;

    AssociationExtensionRepository associationExtensionRepository;

    SnpService snpService;

    public AssociationServiceImpl(AssociationRepository associationRepository,
                                  AssociationMongoRepository associationMongoRepository,
                                  LociAttributesService lociAttributesService,
                                  AssociationAssemblyService associationAssemblyService,
                                  AssociationExtensionRepository associationExtensionRepository,
                                  SnpService snpService) {
        this.associationRepository = associationRepository;
        this.associationMongoRepository = associationMongoRepository;
        this.lociAttributesService = lociAttributesService;
        this.associationAssemblyService = associationAssemblyService;
        this.associationExtensionRepository = associationExtensionRepository;
        this.snpService = snpService;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteAssociation(Long studyId) {
        Long totalAsscns = associationRepository.countByStudyId(studyId);
        Long bucket = totalAsscns/1000;
        for(int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            associationRepository.deleteAll(associationRepository.findByStudyId(studyId, pageable));
        }
    }

    public void saveAssociations(List<Association> associations, Study study) {
        for (Association mongoAssociation : associations) {
            log.info("Association is {}", mongoAssociation.getVariantId());
            uk.ac.ebi.spot.gwas.model.Association association = associationAssemblyService.assemble(mongoAssociation);
            association.setEfoTraits(study.getEfoTraits() != null ? new ArrayList<>(study.getEfoTraits()) : new ArrayList<>());
            association.setBkgEfoTraits(study.getMappedBackgroundTraits() != null ? new ArrayList<>(study.getMappedBackgroundTraits()) : new ArrayList<>());
            lociAttributesService.saveLocusAttributes(association.getLoci());
            AssociationExtension associationExtension = associationAssemblyService.assembleAssociationExtension(mongoAssociation);
            associationRepository.save(association);
            associationExtension.setAssociation(association);
            associationExtensionRepository.save(associationExtension);
            association.setAssociationExtension(associationExtension);
            association.setLastUpdateDate(new Date());
            association.setStudy(study);
            associationRepository.save(association);
        }
    }

    public List<Association> getAssociations(String submissionId, String studyTag) {
        return associationMongoRepository.findBySubmissionIdAndStudyTag(submissionId, studyTag);
    }
}

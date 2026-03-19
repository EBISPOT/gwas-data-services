package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.BodyOfWork;
import uk.ac.ebi.spot.gwas.model.UnpublishedAncestry;
import uk.ac.ebi.spot.gwas.model.UnpublishedStudy;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.BodyOfWorkRepository;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.UnpublishedAncestryRepository;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.UnpublishedStudyRepository;
import uk.ac.ebi.spot.gwas.submission.service.UnpublishedStudiesRetrieveService;
import uk.ac.ebi.spot.gwas.submission.service.UnpublishedStudiesService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UnpublishedStudiesServiceImpl implements UnpublishedStudiesService {

    UnpublishedStudiesRetrieveService unpublishedStudiesRetrieveService;

    UnpublishedAncestryRepository unpublishedAncestryRepository;

    BodyOfWorkRepository bodyOfWorkRepository;

    UnpublishedStudyRepository unpublishedStudyRepository;

    public UnpublishedStudiesServiceImpl(UnpublishedStudiesRetrieveService unpublishedStudiesRetrieveService,
                                         UnpublishedAncestryRepository unpublishedAncestryRepository,
                                         BodyOfWorkRepository bodyOfWorkRepository,
                                         UnpublishedStudyRepository unpublishedStudyRepository) {
        this.unpublishedStudiesRetrieveService = unpublishedStudiesRetrieveService;
        this.unpublishedAncestryRepository = unpublishedAncestryRepository;
        this.bodyOfWorkRepository = bodyOfWorkRepository;
        this.unpublishedStudyRepository = unpublishedStudyRepository;
    }


    @Transactional
    public void cleanUpUnpublishedStudies(List<String> accessionIds) {
        accessionIds.forEach(accessionId -> {
         UnpublishedStudy unpublishedStudy =  unpublishedStudiesRetrieveService.findByAccession(accessionId);
         if(unpublishedStudy != null) {
             Collection<BodyOfWork> bodyOfWorks = unpublishedStudy.getBodiesOfWork();
             Collection<UnpublishedAncestry> ancestries = unpublishedStudy.getAncestries();
             unpublishedAncestryRepository.deleteAll(ancestries);
             Set<UnpublishedStudy> referencedStudies = new HashSet<>();
             bodyOfWorks.forEach(bodyOfWork -> {
                 referencedStudies.addAll(bodyOfWork.getStudies());
             });
             if (referencedStudies.size() <= 1) {
                 bodyOfWorkRepository.deleteAll(bodyOfWorks);
             }
             unpublishedStudyRepository.delete(unpublishedStudy);
         }
        });
    }



}

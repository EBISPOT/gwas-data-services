package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.model.StudyExtension;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.StudyMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.StudyExtensionRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.*;

import java.util.List;
import java.util.Objects;

@Service
public class StudiesServiceImpl implements StudiesService {

    StudyRepository studyRepository;

    AssociationService associationService;


    AncestryService ancestryService;

    CuratorTrackingService curatorTrackingService;

    NoteService noteService;

    StudyMongoRepository studyMongoRepository;


    StudyExtensionRepository studyExtensionRepository;

    StudyAssemblyService studyAssemblyService;

    SampleService sampleService;

    public StudiesServiceImpl(StudyRepository studyRepository,
                              StudyMongoRepository studyMongoRepository,
                              AssociationService associationService,
                              AncestryService ancestryService,
                              CuratorTrackingService curatorTrackingService,
                              NoteService noteService,
                              StudyExtensionRepository studyExtensionRepository,
                              StudyAssemblyService studyAssemblyService,
                              SampleService sampleService) {
        this.studyRepository = studyRepository;
        this.studyMongoRepository = studyMongoRepository;
        this.associationService = associationService;
        this.ancestryService = ancestryService;
        this.curatorTrackingService = curatorTrackingService;
        this.noteService = noteService;
        this.studyExtensionRepository = studyExtensionRepository;
        this.studyAssemblyService = studyAssemblyService;
        this.sampleService = sampleService;
    }

    public Page<Study> getStudies(String pmid, Pageable pageable) {
        return studyRepository.findByPublicationIdPubmedId(pmid, pageable);
    }

    @Transactional(readOnly = true)
    public Long countStudies(String pmid) {
        return studyRepository.countByPublicationIdPubmedId(pmid);
    }

    public uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId) {
        return studyMongoRepository.findById(studyId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Study findByAccessionId(String accessionId) {
        return studyRepository.findByAccession(accessionId).orElse(null);
    }

    private void deleteStudy(Long studyId) {
        studyRepository.delete(Objects.requireNonNull(studyRepository.findById(studyId).orElse(null)));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteChildrenByStudyId(Long studyId) {
        associationService.deleteAssociation(studyId);
        ancestryService.deleteAncestries(studyId);
        curatorTrackingService.deleteCuratorTrackingHistory(studyId);
        noteService.deleteNotesByStudyId(studyId);
        deleteStudy(studyId);
    }

    public void saveStudy(Study study) {
        studyRepository.save(study);


    }

    public Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                             Curator curator,
                             Publication publication,
                             Submission submission) {

        Study study = studyAssemblyService.assemble(mongoStudy);
        study.setOpenTargets(publication.isOpenTargets());
        study.setUserRequested(publication.isUserRequested());
        StudyExtension studyExtension = studyAssemblyService.assembleStudyExtension(mongoStudy);
        studyExtension.setStudy(study);
        studyExtensionRepository.save(studyExtension);
        study.setStudyExtension(studyExtension);
        studyRepository.save(study);
        List<Association> momngoAsscns = associationService.getAssociations(submission.getId(), study.getStudyTag());
        associationService.saveAssociations(momngoAsscns, study);
        List<Sample> mongoSamples = sampleService.getSamples(submission.getId(), study.getStudyTag());
        sampleService.saveSamples(mongoSamples, study);
        return study;
    }


    public void saveStudyExtension(StudyExtension studyExtension) {
        studyExtensionRepository.save(studyExtension);
    }


}

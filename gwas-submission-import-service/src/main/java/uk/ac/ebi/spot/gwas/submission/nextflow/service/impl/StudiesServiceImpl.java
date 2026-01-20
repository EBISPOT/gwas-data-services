package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.deposition.domain.Note;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.StudyMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.StudyExtensionRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.*;

import java.util.*;

@Slf4j
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

    StudiesRetrieveService studiesRetrieveService;

    public StudiesServiceImpl(StudyRepository studyRepository,
                              StudyMongoRepository studyMongoRepository,
                              AssociationService associationService,
                              AncestryService ancestryService,
                              CuratorTrackingService curatorTrackingService,
                              NoteService noteService,
                              StudyExtensionRepository studyExtensionRepository,
                              StudyAssemblyService studyAssemblyService,
                              SampleService sampleService,
                              StudiesRetrieveService studiesRetrieveService) {
        this.studyRepository = studyRepository;
        this.studyMongoRepository = studyMongoRepository;
        this.associationService = associationService;
        this.ancestryService = ancestryService;
        this.curatorTrackingService = curatorTrackingService;
        this.noteService = noteService;
        this.studyExtensionRepository = studyExtensionRepository;
        this.studyAssemblyService = studyAssemblyService;
        this.sampleService = sampleService;
        this.studiesRetrieveService = studiesRetrieveService;
    }



    @Transactional(readOnly = true)
    public Long countStudies(String pmid) {
        return studyRepository.countByPublicationIdPubmedId(pmid);
    }

    public uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId) {
        return studyMongoRepository.findById(studyId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Study> findByAccessionIds(List<String> accessionIds) {
        return studyRepository.findByAccessionIdIn(accessionIds);
    }


    //@Transactional
    public void deleteStudiesForPublication(List<String> accessionIds) {
      //Long count = studiesRetrieveService.countStudies(publicationId);
      //log.info("The count of studies for publication id {} {}", publicationId, count);
      //long bucket = (count/100);
      //for(int i = 0 ; i <= bucket ; i++) {
          //Pageable pageable = PageRequest.of(i , 100);
          //Page<Study> pagedStudies = studiesRetrieveService.getStudies(publicationId, pageable);
        List<Study> pagedStudies = studiesRetrieveService.findByAccessionIds(accessionIds);
          pagedStudies.forEach(study -> {
              deleteChildrenByStudyId(study.getId());
              deleteStudy(study.getId());
          });
         // }
      }



    public void deleteStudy(Long studyId) {
        log.info("Inside delete study");
        studyRepository.deleteById(studyId);//studyRepository.delete(Objects.requireNonNull(studyRepository.findById(studyId).orElse(null)));
    }


    public void deleteChildrenByStudyId(Long studyId) {
        log.info("Inside delete children for studies");
        associationService.deleteAssociation(studyId);
        ancestryService.deleteAncestries(studyId);
        curatorTrackingService.deleteCuratorTrackingHistory(studyId);
        noteService.deleteNotesByStudyId(studyId);
    }

    public void saveStudy(Study study) {
        studyRepository.save(study);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                             Curator curator,
                             Publication publication,
                             Submission submission) {
            Study study = studyAssemblyService.assemble(mongoStudy);
            study.setOpenTargets(publication.isOpenTargets());
            study.setUserRequested(publication.isUserRequested());
            StudyExtension studyExtension = studyAssemblyService.assembleStudyExtension(mongoStudy);
            studyRepository.save(study);
            studyExtension.setStudy(study);
            studyExtensionRepository.save(studyExtension);
            study.setStudyExtension(studyExtension);
            study.setPublicationId(publication);
            studyRepository.save(study);
            List<Association> momngoAsscns = associationService.getAssociations(submission.getId(), study.getStudyTag());
            associationService.saveAssociations(momngoAsscns, study);
            List<Sample> mongoSamples = sampleService.getSamples(submission.getId(), study.getStudyTag());
            sampleService.saveSamples(mongoSamples, study);
            List<Note> mongoNotes = noteService.getNotes(submission.getId(), study.getStudyTag());
            for(Note note : mongoNotes) {
                StudyNote studyNote = noteService.createStudyNote(study, note, curator);
                study.addNote(studyNote);
                studyRepository.save(study);
            };

        return study;
    }


    public void saveStudyExtension(StudyExtension studyExtension) {
        studyExtensionRepository.save(studyExtension);
    }



}



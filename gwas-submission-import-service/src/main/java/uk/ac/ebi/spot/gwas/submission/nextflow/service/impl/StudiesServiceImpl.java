package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudiesServiceImpl implements StudiesService {

    StudyRepository studyRepository;

    AssociationService associationService;

    NoteService noteService;

    StudyMongoRepository studyMongoRepository;


    StudyExtensionRepository studyExtensionRepository;

    StudyAssemblyService studyAssemblyService;

    SampleService sampleService;

    StudyRepository studyOracleRepository;



    public StudiesServiceImpl(StudyRepository studyRepository,
                              StudyMongoRepository studyMongoRepository,
                              AssociationService associationService,
                              NoteService noteService,
                              StudyExtensionRepository studyExtensionRepository,
                              StudyAssemblyService studyAssemblyService,
                              SampleService sampleService,
                              StudyRepository studyOracleRepository) {
        this.studyRepository = studyRepository;
        this.studyMongoRepository = studyMongoRepository;
        this.associationService = associationService;
        this.noteService = noteService;
        this.studyExtensionRepository = studyExtensionRepository;
        this.studyAssemblyService = studyAssemblyService;
        this.sampleService = sampleService;
        this.studyOracleRepository = studyOracleRepository;
    }


    public uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId) {
        return studyMongoRepository.findById(studyId).orElse(null);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                              Curator curator,
                              Publication publication,
                              Submission submission) {
        Study oracleStudy = studyOracleRepository.findByAccessionIdAndPublicationIdPubmedId(mongoStudy.getAccession(),
                publication.getPubmedId()).orElse(null);
        if(oracleStudy != null){
            return null;
        }
        Study study = studyAssemblyService.assemble(mongoStudy);
        study.setOpenTargets(publication.isOpenTargets());
        study.setUserRequested(publication.isUserRequested());
        StudyExtension studyExtension = studyAssemblyService.assembleStudyExtension(mongoStudy);
        study.getHousekeeping().setCurator(curator);
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


    public void publishSummaryStats(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy, Publication publication) {

        Study study = studyRepository.findByAccessionIdAndPublicationIdPubmedId(mongoStudy.getAccession()
                , publication.getPubmedId()).orElse(null);
        if(study != null) {
            study.setFullPvalueSet(true);
            studyRepository.save(study);
        }
    }

}



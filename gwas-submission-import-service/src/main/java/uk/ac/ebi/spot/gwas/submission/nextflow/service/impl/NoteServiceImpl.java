package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Note;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.NoteMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.NoteRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.NoteSubjectRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CuratorService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.NoteService;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    NoteRepository noteRepository;

    NoteSubjectRepository noteSubjectRepository;

    NoteMongoRepository noteMongoRepository;

    public NoteServiceImpl(NoteRepository noteRepository,
                           NoteSubjectRepository noteSubjectRepository,
                           NoteMongoRepository noteMongoRepository) {
        this.noteRepository = noteRepository;
        this.noteSubjectRepository = noteSubjectRepository;
        this.noteMongoRepository = noteMongoRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteNotesByStudyId(Long studyId) {
        noteRepository.deleteAll(noteRepository.findByStudyId(studyId));
    }


    public StudyNote createStudyNote(Study study, Note note, Curator curator) {
        StudyNote studyNote = new StudyNote();
        studyNote.setTextNote(String.format("%s\n%s", study.getStudyTag(), note.getNote()));
        if(note.getStatus() != null) {
            studyNote.setStatus(Boolean.parseBoolean(note.getStatus()));
        }
        studyNote.setCurator(curator);
        studyNote.setGenericId(study.getId());
        String noteSubject = note.getNoteSubject();
        if(noteSubject != null) {
            studyNote.setNoteSubject(noteSubjectRepository.findBySubjectIgnoreCase(noteSubject).orElse(null));
        } else {
            studyNote.setNoteSubject(noteSubjectRepository.findBySubjectIgnoreCase("System note").orElse(null));
        }
        studyNote.setStudy(study);
        noteRepository.save(studyNote);
        return studyNote;
    }


    public List<Note> getNotes(String submissionId, String studyTag) {
        return noteMongoRepository.findBySubmissionIdAndStudyTag(submissionId, studyTag);
    }

}

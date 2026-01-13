package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.NoteRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {

    NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteNotesByStudyId(Long studyId) {
        noteRepository.deleteAll(noteRepository.findByStudyId(studyId));
    }
}

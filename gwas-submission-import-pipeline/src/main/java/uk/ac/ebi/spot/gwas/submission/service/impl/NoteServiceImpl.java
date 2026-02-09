package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.NoteRepository;
import uk.ac.ebi.spot.gwas.submission.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {

    NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void deleteNotes(Long studyId) {
        noteRepository.deleteAll(noteRepository.findByStudyId(studyId));
    }
}

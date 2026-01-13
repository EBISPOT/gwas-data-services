package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Note;

import java.util.List;

public interface NoteService {

  void deleteNotesByStudyId(Long studyId);

}

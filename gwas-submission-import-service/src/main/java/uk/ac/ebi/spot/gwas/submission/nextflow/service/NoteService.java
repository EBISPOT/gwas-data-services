package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Note;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.model.StudyNote;

import java.util.List;

public interface NoteService {


  StudyNote createStudyNote(Study study, Note note, Curator curator);

  List<Note> getNotes(String submissionId, String studyTag);

}

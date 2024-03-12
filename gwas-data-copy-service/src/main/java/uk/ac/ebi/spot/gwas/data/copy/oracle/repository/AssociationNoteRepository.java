package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;


import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.spot.gwas.data.copy.model.StudyNote;

/**
 * Created by cinzia on 28/03/2017.
 */
@Transactional
public interface AssociationNoteRepository extends NoteBaseRepository<StudyNote> {
}

package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;


import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.data.copy.model.Note;

/**
 * Created by cinzia on 27/03/2017.
 */
@Transactional
public interface NoteRepository extends NoteBaseRepository<Note>{
}

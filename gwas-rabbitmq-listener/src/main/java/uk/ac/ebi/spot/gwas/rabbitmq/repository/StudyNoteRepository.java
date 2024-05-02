package uk.ac.ebi.spot.gwas.rabbitmq.repository;


import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.StudyNote;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.NoteBaseRepository;

/**
 * Created by cinzia on 28/03/2017.
 */
@Transactional

public interface StudyNoteRepository extends NoteBaseRepository<StudyNote> {


}

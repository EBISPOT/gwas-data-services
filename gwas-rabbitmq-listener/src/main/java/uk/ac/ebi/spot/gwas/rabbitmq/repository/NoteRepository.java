package uk.ac.ebi.spot.gwas.rabbitmq.repository;


import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Note;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.NoteBaseRepository;

/**
 * Created by cinzia on 27/03/2017.
 */
@Transactional
public interface NoteRepository extends NoteBaseRepository<Note> {
}

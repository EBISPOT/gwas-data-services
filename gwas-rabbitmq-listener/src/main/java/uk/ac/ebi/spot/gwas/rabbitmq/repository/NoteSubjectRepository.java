package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.NoteSubject;


/**
 * Created by xinhe on 06/04/2017.
 */

public interface NoteSubjectRepository extends JpaRepository<NoteSubject, Long> {

    NoteSubject findBySubjectIgnoreCase(String subject);
}

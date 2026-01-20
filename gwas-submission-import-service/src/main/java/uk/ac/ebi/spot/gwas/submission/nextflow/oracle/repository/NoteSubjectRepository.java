package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.NoteSubject;

import java.util.Optional;

public interface NoteSubjectRepository extends JpaRepository<NoteSubject , Long> {

   Optional<NoteSubject> findBySubjectIgnoreCase(String subject);
}

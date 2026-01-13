package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

   List<Note> findByStudyId(Long studyId);
}

package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Note;

import java.util.List;


public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByIdIn(List<String> noteIds);
    Page<Note> findBySubmissionId(String submissionId, Pageable page);
}

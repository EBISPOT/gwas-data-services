package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Note;

import java.util.List;

public interface NoteMongoRepository extends MongoRepository<Note, Long> {

   List<Note> findBySubmissionIdAndStudyTagIgnoreCase(String submissionId, String studyTag);
}

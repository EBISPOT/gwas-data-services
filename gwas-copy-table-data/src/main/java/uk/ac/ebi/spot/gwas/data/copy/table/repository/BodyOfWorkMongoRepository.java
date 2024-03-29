package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.BodyOfWork;

import java.util.Optional;

public interface BodyOfWorkMongoRepository extends MongoRepository<BodyOfWork, String> {

    Optional<BodyOfWork> findByBowIdAndArchivedAndCreated_UserId(String bowId, boolean archived, String userId);

    Page<BodyOfWork> findByArchivedAndCreated_UserId(boolean archived, String userId, Pageable page);

    Page<BodyOfWork> findByStatusAndArchived(String status, boolean archived, Pageable page);

    Page<BodyOfWork> findByArchived(boolean archived, Pageable pageable);

    Page<BodyOfWork> findByStatusAndArchivedAndCreated_UserId(String status, boolean archived, String userId, Pageable pageable);

    Optional<BodyOfWork> findByBowIdAndArchived(String bodyOfWorkId, boolean archived);
}

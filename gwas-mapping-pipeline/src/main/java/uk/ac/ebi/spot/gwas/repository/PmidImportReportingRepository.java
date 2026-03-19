package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PmidImportReportingRepository extends JpaRepository<PmidImportReporting, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PmidImportReporting> findBySubmissionId(String submissionId);

    List<PmidImportReporting> findByStatus(String status);

}

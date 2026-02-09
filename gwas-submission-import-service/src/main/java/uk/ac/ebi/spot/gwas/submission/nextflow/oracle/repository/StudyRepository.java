package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import uk.ac.ebi.spot.gwas.model.Study;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface StudyRepository extends JpaRepository<Study , Long> {

    Optional<Study>  findByAccessionIdAndPublicationIdPubmedId(String accessionId , String pmid);
}

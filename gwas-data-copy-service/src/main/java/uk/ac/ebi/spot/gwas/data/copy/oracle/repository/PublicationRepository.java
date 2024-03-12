package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uk.ac.ebi.spot.gwas.data.copy.model.Publication;

import java.util.List;


public interface PublicationRepository extends JpaRepository<Publication, Long> {

    Publication findByPubmedId(String pubmedId);


    // THOR to change
    // Custom query to get list of study authors

    @Query(value = "select distinct a.fullname_standard from Publication p, Author a where a.id=p.first_author_id " +
            "order by a.fullname_standard asc",
            nativeQuery = true)
    List<String> findAllStudyAuthors();



    List<Publication> findByFirstAuthorIsNull();

    @Query(value = "select distinct pubmed_id from Publication", nativeQuery = true)
    List<String> findAllPubmedIds();
}

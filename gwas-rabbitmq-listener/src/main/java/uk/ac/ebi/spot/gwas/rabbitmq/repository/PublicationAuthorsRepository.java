package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.model.PublicationAuthors;

import javax.transaction.Transactional;


public interface PublicationAuthorsRepository extends JpaRepository<PublicationAuthors, Long> {


    PublicationAuthors findByAuthorIdAndPublicationIdAndSort(Long author_id, Long publication_id, Integer sort);


    @Modifying
    @Transactional
    @Query(value = "delete from PUBLICATION_AUTHORS pa where pa.publication_id = :publicationId",nativeQuery = true)
    void deleteByPublicationId(@Param("publicationId") Long publicationId);

}

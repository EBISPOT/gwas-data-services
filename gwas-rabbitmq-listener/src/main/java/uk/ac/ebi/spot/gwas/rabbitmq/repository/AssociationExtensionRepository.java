package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.AssociationExtension;


public interface AssociationExtensionRepository extends JpaRepository<AssociationExtension, Long> {}

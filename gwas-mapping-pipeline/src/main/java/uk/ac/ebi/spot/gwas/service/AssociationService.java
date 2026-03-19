package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

import java.util.List;
import java.util.Set;

public interface AssociationService {

     Set<Association> getAssociationBasedOnRsId(String rsId);

     void updateMappingDetails(List<Long> ids);

     void fullRemapping(String outputDir, String errorDir);


     void scheduledRemapping(String outputDir, String errorDir);

     void findAssociationMappingError();

     void mapAssociationsBasedOnPmid(String pmid, String submissionId,  String outputDir, String errorDir, String mode);

     PmidImportReporting savePmidReporting(PmidImportReporting pmidImportReporting);

     void savePmidReporting(String submissionId, String status);
}

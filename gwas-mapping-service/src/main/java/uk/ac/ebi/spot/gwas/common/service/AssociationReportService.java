package uk.ac.ebi.spot.gwas.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.association.Association;
import uk.ac.ebi.spot.gwas.common.model.AssociationReport;
import uk.ac.ebi.spot.gwas.common.repository.AssociationReportRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AssociationReportService {

    @Autowired
    private AssociationReportRepository associationReportRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void processAssociationErrors(Association association, Collection<String> errors) {

        // Lists and maps use to translate errors
        List<String> listOfStandardEnsemblErrors = createStandardErrorList();
        Map<String, String> standardEnsemblErrorToErrorType = createErrorMap();
        Map<String, String> errorToErrorTypeMap = new HashMap<>();

        // Check list of standard errors to see if error returned from pipeline matches standard list
        for (String error : errors) {
            for (String standardEnsemblError : listOfStandardEnsemblErrors) {
                if (error.contains(standardEnsemblError)) {
                    String errorType = standardEnsemblErrorToErrorType.get(standardEnsemblError);
                    errorToErrorTypeMap.put(error, errorType);
                }
                else {
                    // Check error message isn't already in list
                    if (!errorToErrorTypeMap.containsKey(error)) {
                        errorToErrorTypeMap.put(error, "unknown type");
                    }
                }
            }
        }

        // Populate arrays that will hold various errors

        Collection<String> snpErrors = new ArrayList<>();
        Collection<String> snpGeneOnDiffChrErrors = new ArrayList<>();
        Collection<String> noGeneForSymbolErrors = new ArrayList<>();
        Collection<String> restServiceErrors = new ArrayList<>();
        Collection<String> suspectVariationErrors = new ArrayList<>();
        Collection<String> geneErrors = new ArrayList<>();

        for (Map.Entry<String, String> entry : errorToErrorTypeMap.entrySet()) {
            String errorMessage = entry.getKey();
            String errorType = entry.getValue();

            switch (errorType) {
                case "restServiceError":
                    restServiceErrors.add(errorMessage);
                    break;
                case "suspectVariationError":
                    suspectVariationErrors.add(errorMessage);
                    break;
                case "snpError":
                    snpErrors.add(errorMessage);
                    break;
                case "snpGeneOnDiffChrError":
                    snpGeneOnDiffChrErrors.add(errorMessage);
                    break;
                case "noGeneForSymbolError":
                    noGeneForSymbolErrors.add(errorMessage);
                    break;
                case "geneError":
                    geneErrors.add(errorMessage);
                    break;
                default:
                    getLog().warn("For association ID: " + association.getId() +
                                          ", cannot determine error type for error " + errorMessage);
                    break;
            }
        }

        String allSnpErrors = null;
        String allSnpGeneOnDiffChrErrors = null;
        String allNoGeneForSymbolErrors = null;
        String allRestServiceErrors = null;
        String allSuspectVariationErrors = null;
        String allGeneErrors = null;

        if (!snpErrors.isEmpty()) {
            allSnpErrors = String.join(", ", snpErrors);
        }

        if (!snpGeneOnDiffChrErrors.isEmpty()) {
            allSnpGeneOnDiffChrErrors = String.join(", ", snpGeneOnDiffChrErrors);
        }

        if (!noGeneForSymbolErrors.isEmpty()) {
            allNoGeneForSymbolErrors = String.join(", ", noGeneForSymbolErrors);
        }

        if (!restServiceErrors.isEmpty()) {
            allRestServiceErrors = String.join(", ", restServiceErrors);
        }

        if (!suspectVariationErrors.isEmpty()) {
            allSuspectVariationErrors = String.join(", ", suspectVariationErrors);
        }

        if (!geneErrors.isEmpty()) {
            allGeneErrors = String.join(", ", geneErrors);
        }

        // Create association report object
        AssociationReport associationReport = new AssociationReport();
        associationReport.setLastUpdateDate(new Date());
        associationReport.setSnpError(allSnpErrors);
        associationReport.setSnpGeneOnDiffChr(allSnpGeneOnDiffChrErrors);
        associationReport.setNoGeneForSymbol(allNoGeneForSymbolErrors);
        associationReport.setRestServiceError(allRestServiceErrors);
        associationReport.setSuspectVariationError(allSuspectVariationErrors);
        associationReport.setGeneError(allGeneErrors);

        // Before setting link to association check for any existing reports linked to this association
        AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
        log.info("Checking existing report for association: {}", association.getId());

        if (existingReport != null) {
            associationReportRepository.delete(existingReport);
        }
        associationReport.setAssociation(association);

        // Save association report
        associationReportRepository.deleteByAssociationId(association.getId());
        associationReportRepository.save(associationReport);

    }

    /**
     * Creates a list of common errors
     */
    private List<String> createStandardErrorList() {
        List<String> standardErrorList = new ArrayList<>();

        // REST service error
        standardErrorList.add("No server is available to handle this request");
        standardErrorList.add("Web service");
        standardErrorList.add("No data available");

        // SNP Error
        standardErrorList.add("not found for homo_sapiens");
        standardErrorList.add("Attempt to map SNP");

        // Suspect variation errors
        standardErrorList.add("Variation does not map to the genome");
        standardErrorList.add("Variant maps to more than 1 location");
        standardErrorList.add("None of the variant alleles match the reference allele");
        standardErrorList.add("Variation has more than 3 different alleles");
        standardErrorList.add("Flagged as suspect by dbSNP");
        standardErrorList.add("Variant can not be re-mapped to the current assembly");

        // Gene error
        standardErrorList.add("Can't find a location in Ensembl for the reported gene");
        standardErrorList.add("no mapping available for the variant");

        // Snp Gene on different chromosome error
        standardErrorList.add("is on a different chromosome");

        // No gene for symbol error
        standardErrorList.add("No valid lookup found for symbol");

        return standardErrorList;
    }

    /**
     * Creates a map of common errors and there types
     */
    private Map<String, String> createErrorMap() {

        Map<String, String> errorMap = new HashMap<>();

        // REST service error
        errorMap.putIfAbsent("No server is available to handle this request", "restServiceError");
        errorMap.putIfAbsent("Web service", "restServiceError");
        errorMap.putIfAbsent("No data available", "restServiceError");

        // Add suspect variation errors that usually result from a snp not mapping
        errorMap.putIfAbsent("Variation does not map to the genome", "suspectVariationError");
        errorMap.putIfAbsent("Variant maps to more than 1 location", "suspectVariationError");
        errorMap.putIfAbsent("None of the variant alleles match the reference allele", "suspectVariationError");
        errorMap.putIfAbsent("Variation has more than 3 different alleles", "suspectVariationError");
        errorMap.putIfAbsent("Flagged as suspect by dbSNP", "suspectVariationError");
        errorMap.putIfAbsent("Variant can not be re-mapped to the current assembly", "suspectVariationError");

        // Catch other common errors
        errorMap.putIfAbsent("Can't find a location in Ensembl for the reported gene", "geneError");
        errorMap.putIfAbsent("no mapping available for the variant", "geneError");

        errorMap.putIfAbsent("not found for homo_sapiens", "snpError");
        errorMap.putIfAbsent("Attempt to map SNP", "snpError");

        errorMap.putIfAbsent("is on a different chromosome", "snpGeneOnDiffChrError");

        errorMap.putIfAbsent("No valid lookup found for symbol", "noGeneForSymbolError");

        return errorMap;
    }

    /**
     * This method is used when the mapping pipeline returns no errors. It removes any existing association reports and
     * replaces with a new one with no errors. This ensures errors from previous runs of the mapping pipeline do not
     * remain in database if they are no longer appearing as mapping pipeline errors.
     *
     * @param association association, used to create new association report
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateAssociationReportDetails(Association association) {

        // Create association report object
        AssociationReport associationReport = new AssociationReport();
        associationReport.setLastUpdateDate(new Date());
        associationReport.setAssociation(association);

        // Before setting link to association remove any existing reports linked to this association
        AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
        if (existingReport != null) {
            associationReport.setId(existingReport.getId());
        }

        // Save association report
        associationReportRepository.save(associationReport);
    }

    public void reportCheck(List<Association> associations){
        List<Association> noErrorAssociations = new ArrayList<>();
        AtomicInteger counta = new AtomicInteger(1);

        associations.forEach(association -> {
            AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
            if (existingReport == null) {
                noErrorAssociations.add(association);
            }
            log.info("Checked Report for {}, {} of {}", association.getId(), counta.getAndIncrement(), associations.size());
        });

        AtomicInteger index = new AtomicInteger(1);
        noErrorAssociations.forEach(association -> {
            try {
                processAssociationErrors(association, new ArrayList<>());
                log.info("Saved Error Report {} : {}", index.get(), association.getId());
            }catch (Exception e){
                log.info("{} could not be saved {}", association.getId(), e.getMessage());
            }
            index.getAndIncrement();
        });
    }
}


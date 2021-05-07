package uk.ac.ebi.spot.gwas.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class AssociationReport {
    @Id
    @GeneratedValue
    private Long id;

    private Date lastUpdateDate;

    private String snpError;

    private String snpGeneOnDiffChr;

    private String noGeneForSymbol;

    private String restServiceError;

    private String suspectVariationError;

    private String geneError;

    private Boolean errorCheckedByCurator = false;

    @OneToOne
    private Association association;

    // JPA no-args constructor
    public AssociationReport() {
    }

    public AssociationReport(Date lastUpdateDate,
                             String snpError,
                             String snpGeneOnDiffChr,
                             String noGeneForSymbol,
                             String restServiceError,
                             String suspectVariationError,
                             String geneError,
                             Boolean errorCheckedByCurator,
                             Association association) {
        this.lastUpdateDate = lastUpdateDate;
        this.snpError = snpError;
        this.snpGeneOnDiffChr = snpGeneOnDiffChr;
        this.noGeneForSymbol = noGeneForSymbol;
        this.restServiceError = restServiceError;
        this.suspectVariationError = suspectVariationError;
        this.geneError = geneError;
        this.errorCheckedByCurator = errorCheckedByCurator;
        this.association = association;
    }

    public Long getId() {
        return id;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getSnpError() {
        return snpError;
    }

    public String getSnpGeneOnDiffChr() {
        return snpGeneOnDiffChr;
    }

    public String getNoGeneForSymbol() {
        return noGeneForSymbol;
    }

    public String getRestServiceError() {
        return restServiceError;
    }

    public String getSuspectVariationError() {
        return suspectVariationError;
    }

    public String getGeneError() {
        return geneError;
    }

    public Boolean getErrorCheckedByCurator() {
        return errorCheckedByCurator;
    }

    @JsonIgnore
    public Association getAssociation() {
        return association;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setSnpError(String snpError) {
        this.snpError = snpError;
    }

    public void setSnpGeneOnDiffChr(String snpGeneOnDiffChr) {
        this.snpGeneOnDiffChr = snpGeneOnDiffChr;
    }

    public void setNoGeneForSymbol(String noGeneForSymbol) {
        this.noGeneForSymbol = noGeneForSymbol;
    }

    public void setRestServiceError(String restServiceError) {
        this.restServiceError = restServiceError;
    }

    public void setSuspectVariationError(String suspectVariationError) {
        this.suspectVariationError = suspectVariationError;
    }

    public void setGeneError(String geneError) {
        this.geneError = geneError;
    }

    public void setErrorCheckedByCurator(Boolean errorCheckedByCurator) {
        this.errorCheckedByCurator = errorCheckedByCurator;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}


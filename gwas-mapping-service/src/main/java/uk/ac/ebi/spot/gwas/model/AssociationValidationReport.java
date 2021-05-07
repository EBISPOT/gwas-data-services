package uk.ac.ebi.spot.gwas.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class AssociationValidationReport {

    @Id
    @GeneratedValue
    private Long id;

    private String warning;

    private String validatedField;

    @ManyToOne
    private Association association;

    public AssociationValidationReport() {
    }

    public AssociationValidationReport(String warning,
                                       String validatedField,
                                       Association association) {
        this.warning = warning;
        this.validatedField = validatedField;
        this.association = association;
    }

    public Long getId() {
        return id;
    }

    public String getWarning() {
        return warning;
    }

    public String getValidatedField() {
        return validatedField;
    }

    @JsonIgnore
    public Association getAssociation() {
        return association;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public void setValidatedField(String validatedField) {
        this.validatedField = validatedField;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}

package uk.ac.ebi.spot.gwas.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by cinzia on 28/03/2017.
 */
@Entity
@DiscriminatorValue(value = "Study")
public class StudyNote extends Note {

    public StudyNote() {
        super();
    }

    public StudyNote(Study study) {
        super(study);
        this.setGenericId(study.getId());
    }


}


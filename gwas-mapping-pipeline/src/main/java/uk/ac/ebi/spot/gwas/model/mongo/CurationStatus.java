package uk.ac.ebi.spot.gwas.model.mongo;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode
@Document(collection = "curationStatus")
public class CurationStatus {

    @Id
    private String id;

    @Indexed
    private String status;

    public CurationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }
}

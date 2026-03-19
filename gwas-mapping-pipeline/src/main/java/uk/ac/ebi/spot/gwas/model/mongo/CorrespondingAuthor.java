package uk.ac.ebi.spot.gwas.model.mongo;

import java.io.Serializable;

public class CorrespondingAuthor implements Serializable {

    private static final long serialVersionUID = -7469732232884595250L;

    private String authorName;

    private String email;

    public CorrespondingAuthor(String authorName, String email) {
        this.authorName = authorName;
        this.email = email;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getEmail() {
        return email;
    }
}

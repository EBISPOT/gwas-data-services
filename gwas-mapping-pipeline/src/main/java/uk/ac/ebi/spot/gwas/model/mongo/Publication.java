package uk.ac.ebi.spot.gwas.model.mongo;

import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "publications")
public class Publication implements Serializable {

    private static final long serialVersionUID = -6614581853183999938L;

    @Id
    private String id;

    @Indexed
    private String pmid;

    private String status;

    private String journal;

    private String title;

    @TextIndexed
    private String firstAuthor;

    private LocalDate publicationDate;

    private CorrespondingAuthor correspondingAuthor;

    private String curatorId;

    private String submitter;

    @Indexed
    private boolean pushed;

    private List<String> authors;

    private String firstAuthorId;

    private String curationStatusId;

    private Provenance created;

    private Provenance updated;

    private Boolean isUserRequested;

    private Boolean isOpenTargets;

    public Publication() {

    }

    public Publication(String pmid, String journal, String title, String firstAuthor,
                       LocalDate publicationDate, CorrespondingAuthor correspondingAuthor,
                       String status, String curatorId, String curationStatusId, String submitter,
                       Provenance created, Provenance updated, List<String> authors, Boolean isUserRequested, Boolean isOpenTargets) {
        this.pmid = pmid;
        this.journal = journal;
        this.title = title;
        this.firstAuthor = firstAuthor;
        this.publicationDate = publicationDate;
        this.correspondingAuthor = correspondingAuthor;
        this.status = status;
        this.pushed = false;
        this.curatorId = curatorId;
        this.curationStatusId = curationStatusId;
        this.submitter = submitter;
        this.created = created;
        this.updated = updated;
        this.authors = authors;
        this.isUserRequested = isUserRequested;
        this.isOpenTargets = isOpenTargets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public CorrespondingAuthor getCorrespondingAuthor() {
        return correspondingAuthor;
    }

    public void setCorrespondingAuthor(CorrespondingAuthor correspondingAuthor) {
        this.correspondingAuthor = correspondingAuthor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    public String getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(String curatorId) {
        this.curatorId = curatorId;
    }

    public String getCurationStatusId() {
        return curationStatusId;
    }

    public void setCurationStatusId(String curationStatusId) {
        this.curationStatusId = curationStatusId;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getFirstAuthorId() {
        return firstAuthorId;
    }

    public void setFirstAuthorId(String firstAuthorId) {
        this.firstAuthorId = firstAuthorId;
    }

    public Provenance getCreated() {
        return created;
    }

    public void setCreated(Provenance created) {
        this.created = created;
    }

    public Provenance getUpdated() {
        return updated;
    }

    public void setUpdated(Provenance updated) {
        this.updated = updated;
    }

    public Boolean getIsUserRequested() {
        return isUserRequested;
    }

    public void setIsUserRequested(Boolean isUserRequested) {
        this.isUserRequested = isUserRequested;
    }

    public Boolean getIsOpenTargets() {
        return isOpenTargets;
    }

    public void setIsOpenTargets(Boolean isOpenTargets) {
        this.isOpenTargets = isOpenTargets;
    }

}

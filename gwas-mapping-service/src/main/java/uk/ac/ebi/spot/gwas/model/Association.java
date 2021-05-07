package uk.ac.ebi.spot.gwas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
public class Association implements Trackable {
    @Id
    @GeneratedValue
    private Long id;

    private String riskFrequency;

    private String pvalueDescription;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Boolean multiSnpHaplotype = false;

    private Boolean snpInteraction = false;

    @JsonIgnore
    private Boolean snpApproved = false;

    private String snpType;

    private Float standardError;

    //@Column(name = "association_range")
    private String range;

    private String description;

    // OR specific values
    private Float orPerCopyNum;

    @JsonIgnore
    private Float orPerCopyRecip;

    @JsonIgnore
    private String orPerCopyRecipRange;

    // Beta specific values
    private Float betaNum;

    private String betaUnit;

    private String betaDirection;

    // Association can have a number of loci attached depending on whether its a multi-snp haplotype
    // or SNP:SNP interaction
    @OneToMany
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCUS_ID"))
    private Collection<Locus> loci = new ArrayList<>();

    @OneToOne(mappedBy = "association", orphanRemoval = true)
    private AssociationReport associationReport;

    @OneToOne(mappedBy = "association", orphanRemoval = true, optional=true, cascade = CascadeType.ALL)
    private AssociationExtension associationExtension;

    @OneToMany(mappedBy = "association", orphanRemoval = true)
    private Collection<AssociationValidationReport> associationValidationReports = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    @JsonIgnore
    private String lastMappingPerformedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinTable(name = "ASSOCIATION_EVENT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();


    /**REST API fix: reversal of control of association-SNP and association-gene relationship from association to SNP/gene to fix deletion issues with respect to
     * the association-SNP/gene view table. Works but not optimal, improve solution if possible**/
//    @ManyToMany
//    @JoinTable(name = "ASSOCIATION_SNP_VIEW",
//               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
//               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    @ManyToMany(mappedBy = "associations")
    private Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

//    @ManyToMany
//    @JoinTable(name = "ASSOCIATION_GENE_VIEW",
//               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
//               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
    @ManyToMany(mappedBy = "associations")
    private Collection<Gene> genes = new ArrayList<>();


    @PrePersist
    protected void onCreate() { lastUpdateDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { lastUpdateDate = new Date(); }

    // JPA no-args constructor
    public Association() {
    }

    public Association(Long id){
        this.id = id;
    }

    public Association(String riskFrequency,
                       String pvalueDescription,
                       Integer pvalueMantissa,
                       Integer pvalueExponent,
                       Boolean multiSnpHaplotype,
                       Boolean snpInteraction,
                       Boolean snpApproved,
                       String snpType,
                       Float standardError,
                       String range,
                       String description,
                       Float orPerCopyNum,
                       Float orPerCopyRecip,
                       String orPerCopyRecipRange,
                       Float betaNum,
                       String betaUnit,
                       String betaDirection,
                       Collection<Locus> loci,
                       AssociationReport associationReport,
                       Collection<AssociationValidationReport> associationValidationReport,
                       Date lastMappingDate,
                       String lastMappingPerformedBy,
                       Date lastUpdateDate,
                       Collection<Event> events,
                       Collection<SingleNucleotidePolymorphism> snps,
                       Collection<Gene> genes) {
        this.riskFrequency = riskFrequency;
        this.pvalueDescription = pvalueDescription;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.snpType = snpType;
        this.standardError = standardError;
        this.range = range;
        this.description = description;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.betaNum = betaNum;
        this.betaUnit = betaUnit;
        this.betaDirection = betaDirection;
        this.loci = loci;
        this.associationReport = associationReport;
        this.associationValidationReports = associationValidationReport;
        this.lastMappingDate = lastMappingDate;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastUpdateDate = lastUpdateDate;
        this.events = events;
        this.snps = snps;
        this.genes = genes;
    }


    public Long getId() {
        return id;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public String getPvalueDescription() {
        return pvalueDescription;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public Boolean getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public Boolean getSnpInteraction() {
        return snpInteraction;
    }

    public Boolean getSnpApproved() {
        return snpApproved;
    }

    public String getSnpType() {
        return snpType;
    }

    public Float getStandardError() {
        return standardError;
    }

    public String getRange() {
        return range;
    }

    public String getDescription() {
        return description;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public Float getBetaNum() {
        return betaNum;
    }

    public String getBetaUnit() {
        return betaUnit;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    @JsonIgnore
    public Collection<Locus> getLoci() {
        return loci;
    }

    @JsonIgnore
    public AssociationReport getAssociationReport() {
        return associationReport;
    }

    @JsonIgnore
    public AssociationExtension getAssociationExtension() {
        return associationExtension;
    }

    @JsonIgnore
    public Collection<AssociationValidationReport> getAssociationValidationReports() {
        return associationValidationReports;
    }

    public Date getLastMappingDate() {
        return lastMappingDate;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @JsonIgnore
    public Collection<Event> getEvents() {
        return events;
    }

    @JsonIgnore
    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    @JsonIgnore
    public Collection<Gene> getGenes() {
        return genes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public void setPvalueDescription(String pvalueDescription) {
        this.pvalueDescription = pvalueDescription;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public void setMultiSnpHaplotype(Boolean multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public void setSnpInteraction(Boolean snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public void setSnpApproved(Boolean snpApproved) {
        this.snpApproved = snpApproved;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public void setBetaNum(Float betaNum) {
        this.betaNum = betaNum;
    }

    public void setBetaUnit(String betaUnit) {
        this.betaUnit = betaUnit;
    }

    public void setBetaDirection(String betaDirection) {
        this.betaDirection = betaDirection;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    public void setAssociationReport(AssociationReport associationReport) {
        this.associationReport = associationReport;
    }

    public void setAssociationExtension(AssociationExtension associationExtension) {
        this.associationExtension = associationExtension;
    }

    public void setAssociationValidationReports(Collection<AssociationValidationReport> associationValidationReports) {
        this.associationValidationReports = associationValidationReports;
    }

    public void setLastMappingDate(Date lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }

    public void setGenes(Collection<Gene> genes) {
        this.genes = genes;
    }

    @Override
    public synchronized void addEvent(Event event) {
        Collection<Event> currentEvents = getEvents();
        currentEvents.add(event);
        setEvents((currentEvents));
    }

}

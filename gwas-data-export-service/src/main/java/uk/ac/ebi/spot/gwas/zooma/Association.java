package uk.ac.ebi.spot.gwas.zooma;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Association {

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

    @ManyToOne
    private Study study;

    // Association can have a number of loci attached depending on whether its a multi-snp haplotype
    // or SNP:SNP interaction
    @OneToMany
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCUS_ID"))
    private Collection<Locus> loci = new ArrayList<>();

    // To avoid null values collections are by default initialized to an empty array list
    @ManyToMany
    @JoinTable(name = "ASSOCIATION_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    @JsonIgnore
    private String lastMappingPerformedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    @ManyToMany(mappedBy = "associations")
    private Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

    @PrePersist
    protected void onCreate() { lastUpdateDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { lastUpdateDate = new Date(); }

}

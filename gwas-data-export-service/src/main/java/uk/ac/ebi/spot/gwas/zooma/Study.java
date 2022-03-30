package uk.ac.ebi.spot.gwas.zooma;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Study {
    @Id
    @GeneratedValue
    private Long id;

    private String initialSampleSize;

    private String replicateSampleSize;

    @JsonIgnore
    private Boolean cnv = false;

    private Boolean gxe = false;

    private Boolean gxg = false;

    private Integer snpCount;

    private String qualifier;

    private Boolean imputed = false;

    private Boolean pooled = false;

    private String studyDesignComment;

    private String accessionId;

    private Boolean fullPvalueSet = false;

    private Boolean userRequested = false;

    private Boolean openTargets = false;

    @OneToMany(mappedBy = "study")
    private Collection<Association> associations;

    @OneToOne(cascade = {CascadeType.ALL})
    @JsonManagedReference("publicationInfo")
    @JoinColumn(name = "publication_id")
    private Publication publicationId;

    @ManyToOne
    @JoinTable(name = "STUDY_DISEASE_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "DISEASE_TRAIT_ID"))
    private DiseaseTrait diseaseTrait;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "STUDY_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits;

    @ManyToOne(optional = true)
    @JoinTable(name = "STUDY_BACKGROUND_TRAIT",
            joinColumns = @JoinColumn(name = "STUDY_ID"),
            inverseJoinColumns = @JoinColumn(name = "DISEASE_TRAIT_ID"))
    private DiseaseTrait backgroundTrait;

    @ManyToMany()
    @JoinTable(name = "STUDY_BACKGROUND_EFO_TRAIT",
            joinColumns = @JoinColumn(name = "STUDY_ID"),
            inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> mappedBackgroundTraits;

    @ManyToMany(mappedBy = "studies")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SingleNucleotidePolymorphism> snps = new ArrayList<>();


    private String studyTag;

    private Boolean agreedToCc0;

}

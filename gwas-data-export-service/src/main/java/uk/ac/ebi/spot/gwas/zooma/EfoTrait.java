package uk.ac.ebi.spot.gwas.zooma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EfoTrait {
    @Id
    @GeneratedValue
    private Long id;

    private String trait;

    private String uri;

    private String shortForm;

    @ManyToMany(mappedBy = "efoTraits")
    private Collection<Study> studies;

    @ManyToMany(mappedBy = "efoTraits")
    private Collection<Association> associations;

}

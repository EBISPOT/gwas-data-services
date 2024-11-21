package uk.ac.ebi.spot.gwas.assembly_info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyInfo {

    @JsonProperty("coordinate_system")
    public String coordinateSystem;

    @JsonProperty("assembly_exception_type")
    public String assemblyExceptionType;

    @JsonProperty("is_circular")
    public Integer isCircular;

    @JsonProperty("assembly_name")
    public String assemblyName;

    @JsonProperty("length")
    public Integer length;

    @JsonProperty("is_chromosome")
    public Integer isChromosome;

}



package uk.ac.ebi.spot.gwas.constant;

public enum DataType {

    VARIATION(Location.VARIATION_FILE),

    REPORTED_GENES(Location.REPORTED_GENES_FILE),

    CYTOGENETIC_BAND(Location.CYTOGENETIC_BAND_FILE),

    ASSEMBLY_INFO(Location.ASSEMBLY_INFO_FILE);

    private final String value;

    DataType(String value) {
        this.value = value;
    }

    public String getFileLocation() {
        return value;
    }
}

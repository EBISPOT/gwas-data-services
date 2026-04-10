package uk.ac.ebi.spot.gwas.submission.constants;

public enum SubmissionType {

    METADATA("Metadata"),
    METADATA_AND_SUM_STATS("Metadata and Summary Stats"),
    SUM_STATS("Summary Stats"),
    METADATA_AND_TOP_ASSOCIATIONS("Metadata and Top Associations"),
    METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS("Metadata Summary Stats and Top Associations"),
    PRE_PUBLISHED("Pre-Publication"),
    UNKNOWN("Unknown");
    public final String label;

    private SubmissionType(String label) {
        this.label = label;
    }
}

package uk.ac.ebi.spot.gwas.util;

import org.apache.commons.cli.*;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -m option to run mapping on contents of GWAS catalog";

    public static final String MAP_ALL_SNPS_INDB_OPT = "r";
    public static final String MAP_ALL_SNPS_INDB_LONG_OPT = "remapping";
    public static final String MAP_ALL_DESC = "Maps all associations in the GWAS database. Mapping pipeline will map SNPs " +
            "in database and also validate the author reported gene linked to that SNP via the associations";

    public static final String MAP_SOME_SNPS_INDB_OPT = "m";
    public static final String MAP_SOME_SNPS_INDB_LONG_OPT = "mapping";
    public static final String MAP_SOME_DESC = "Maps some associations in the GWAS database. Mapping pipeline will map SNPs " +
            "in database and also validate the author reported gene linked to that SNP via the associations";

    public static final String CACHE_OPT = "c";
    public static final String CACHE_LONG_OPT = "cache";
    public static final String CACHE_DESCRIPTION = "-c [thread-size] : Retrieves all the needed mapping dataset from Ensembl without mapping the associations";

    private CommandUtil() {
        // Hide implicit public constructor
    }

    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option mappingOption = new Option(MAP_ALL_SNPS_INDB_OPT, MAP_ALL_SNPS_INDB_LONG_OPT, false, MAP_ALL_DESC);
        Option mappingOptionLSF = new Option(MAP_SOME_SNPS_INDB_OPT, MAP_SOME_SNPS_INDB_LONG_OPT, false, MAP_SOME_DESC);
        Option cacheOption = new Option(CACHE_OPT, CACHE_LONG_OPT, false, CACHE_DESCRIPTION);

        mappingOption.setArgName("performer");
        mappingOptionLSF.setArgName("performer");
        cacheOption.setArgName("thread-size");

        mappingOption.setRequired(true);
        mappingOptionLSF.setRequired(true);

        modeGroup.addOption(mappingOption);
        modeGroup.addOption(mappingOptionLSF);

        options.addOption(helpOption);
        options.addOption(cacheOption);
        options.addOptionGroup(modeGroup);
        options.addOptionGroup(modeGroup);

        return options;
    }

}

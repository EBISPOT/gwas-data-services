package uk.ac.ebi.spot.gwas.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -m study|association option to populate parent trait for study or association";

    public static final String EXEC_MODE_OPT = "m";

    public static final String EXEC_MODE_OPT_LONG = "mode";
    public static final String EXEC_MODE_OPT_DESC = "-m [mode] mode full or large";

    public static final String EXEC_EFOTRAITS = "e";

    public static final String EXEC_EFOTRAITS_DESC = "-e [PubIds] List of EFOIDs seperate by commas ";

    public static final String EXEC_EFOTRAITS_LONG = "EFO Shorforms";


    public static final String EXEC_MODE_PARENT_EFO = "p";

    public static final String EXEC_MODE_PARENT_EFO_LONG = "parent";

    public static final String EXEC_MODE_PARENT_EFO_DESC = "-p [parent] EFO shortform";


    public CommandUtil() {
    }

    public static Options bindOptions() {

        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(false);
        Options options = new Options();

        Option modeOption = new Option(EXEC_MODE_OPT, EXEC_MODE_OPT_LONG, true, EXEC_MODE_OPT_DESC);
        Option efoIdsOption = new Option(EXEC_EFOTRAITS, EXEC_EFOTRAITS_LONG, true, EXEC_EFOTRAITS_DESC );
        Option parentOption = new Option(EXEC_MODE_PARENT_EFO, EXEC_MODE_PARENT_EFO_LONG, true, EXEC_MODE_PARENT_EFO_DESC );
        options.addOption(modeOption);
        options.addOption(efoIdsOption);
        options.addOption(parentOption);
        options.addOptionGroup(optionGroup);
        return options;
    }
}

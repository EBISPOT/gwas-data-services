package uk.ac.ebi.spot.gwas.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -o output to write to output file path& -e for error file path";
    public static final String EXEC_OUT_FILE = "o";
    public static final String EXEC_OUT_FILE_LONG = "outputfileDir";
    public static final String EXEC_OUT_FILE_DESC = "-o [outputfile Dir] for LSF jobs of Parent Trait Mapper";
    public static final String EXEC_ERR_FILE = "e";
    public static final String EXEC_ERR_FILE_LONG = "errFileDir";
    public static final String EXEC_ERR_FILE_DESC = "-e [Error File] for LSF Jobs of Parent Trait Mappe";

    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();
        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option outputfileDirOption = new Option(EXEC_OUT_FILE, EXEC_OUT_FILE_LONG, true, EXEC_OUT_FILE_DESC);
        Option errfileDirOption = new Option(EXEC_ERR_FILE, EXEC_ERR_FILE_LONG, true, EXEC_ERR_FILE_DESC);
        options.addOption(outputfileDirOption);
        options.addOption(helpOption);
        options.addOption(errfileDirOption);
        options.addOptionGroup(modeGroup);
        return options;
    }

}

package uk.ac.ebi.spot.gwas.submission.nextflow.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -r option to run batch im[ort of studiesof GWAS catalog";
    public static final String EXEC_STUDYIDS = "g";
    public static final String EXEC_STUDYIDS_LONG = "studyIds";
    public static final String EXEC_STUDYIDS_DESC = "-g [studyIds to import] 6852e831ae4ad100012c629f";
    public static final String EXEC_SUBMISSIONID = "s";
    public static final String EXEC_SUBMISSIONID_LONG = "submissionId";
    public static final String EXEC_SUBMISSIONID_DESC = "-s [submissionId to import] 684af87a88ee4e000190d923";
    public static final String EXEC_SUBMISSION_TYPE = "t";
    public static final String EXEC_SUBMISSION_TYPE_LONG = "type";
    public static final String EXEC_SUBMISSION_TYPE_DESC = "-t [submission type] METADATA";
    public static final String EXEC_CURATOR_ID = "c";
    public static final String EXEC_CURATOR_ID_LONG = "curator";
    public static final String EXEC_CURATOR_ID_DESC = "-c [curator emailId] Curator Email Id";



    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option studyIdsOption = new Option(EXEC_STUDYIDS, EXEC_STUDYIDS_LONG, true, EXEC_STUDYIDS_DESC);
        Option submissionIdOption = new Option(EXEC_SUBMISSIONID, EXEC_SUBMISSIONID_LONG, true, EXEC_SUBMISSIONID_DESC);
        Option submissionTypeOption = new Option(EXEC_SUBMISSION_TYPE, EXEC_SUBMISSION_TYPE_LONG, true, EXEC_SUBMISSION_TYPE_DESC);
        Option curatorOption = new Option(EXEC_CURATOR_ID, EXEC_CURATOR_ID_LONG, true, EXEC_CURATOR_ID_DESC);
        options.addOption(helpOption);
        options.addOption(studyIdsOption);
        options.addOption(submissionIdOption);
        options.addOption(submissionTypeOption);
        options.addOption(curatorOption);
        return options;
    }
}

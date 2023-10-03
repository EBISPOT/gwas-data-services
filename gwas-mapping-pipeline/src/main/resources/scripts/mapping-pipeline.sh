#!/bin/sh

# Capture absolute path for the script:
scriptDir=${0%/*}/;
mode=${1}
# Links to files: Needs to be updated for all the environments:
jarLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub
jvmparams='-Xms4096m -Xmx4096m'


# Print out help message:
function display_help(){

    echo "This is the updated wrapper script for the solr indexer application."
    echo ""
    echo "Usage:"
    echo "  $0 -o /hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/logs/bsub/  -e /hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/logs/bsub/  -r rs11064157,rs11136000 -h"
    echo ""
    echo "Where:"
    echo "  -o      - Output for mapping pipeline bsub Jobs"
    echo "  -e      - Output for mapping pipeline bsub Jobs"
    echo "  -r      - Rsid as parameter "
    echo "  -h      - Show this help message and exit"
    echo ""
    echo ""

    exit 1;
}

# Addintional parameters will be collected uder this variable:
documentParameters="${jvmparams}"
echo "documentParameters is ${documentParameters}"

rm -rf ${logslocation}/*

java -DentityExpansionLimit=100000000 -Dspring.profiles.active=cluster -Dexecutor.thread-pool.count=10 -Dassociation.partition.size=15 \
    ${documentParameters} \
    -jar ${jarLocation}/gwas-mapping-pipeline.jar -m ${mode} -i ${jarLocation}/Incorrecly_mapped_variants.tsv -o ${logslocation} -e ${logslocation}


# Capture exit code:
exit $?
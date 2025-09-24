#!/bin/sh

# Capture absolute path for the script:
scriptDir=${0%/*}/;

# Links to files: Needs to be updated for all the environments:
jarLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/gwas-rabbitmq-listener
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/gwas-rabbitmq-listener/logs/bsub
jvmparams='-Xms4096m -Xmx4096m'
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/gwas-rabbitmq-listener/config
source ${configlocation}/db-env

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

export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PWD}

java -DentityExpansionLimit=100000000 -Dspring.profiles.active=sandbox-migration -Dspring.datasource.username=${ORACLE_DB_USER} -Dspring.datasource.password=${ORACLE_DB_PWD} \
   -Dspring.rabbitmq.username=${RABBIT_USER} -Dspring.rabbitmq.password=${RABBIT_PWD} \
    ${documentParameters} \
    -jar ${jarLocation}/gwas-rabbitmq-listener.jar


# Capture exit code:
exit $?
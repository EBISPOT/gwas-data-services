#!/bin/sh

# Capture absolute path for the script:
scriptDir=${0%/*}/;
submissionid=${1}
pmid=${2}
curator=${3}
type=${4}
studyids=${5}


jarLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission
jvmparams='-Xms4096m -Xmx4096m'
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission/config
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/import-submission
source ${configlocation}/db-env

echo "studyids is ${studyids}"

export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PASSWORD}

java -DentityExpansionLimit=100000000 -Dspring.profiles.active=cluster  -Dspring.datasource.username=${ORACLE_DB_USER} -Dspring.datasource.password=${ORACLE_DB_PWD}  \
    ${documentParameters} \
    -jar ${jarLocation}/gwas-submission-import-service.jar -s ${submissionid} -p ${pmid} -c ${curator} -g ${studyids} -t ${type}

exit $?
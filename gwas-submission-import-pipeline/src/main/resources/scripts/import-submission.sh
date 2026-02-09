#!/bin/sh

# Capture absolute path for the script:
scriptDir=${0%/*}/;
#submissionid=${1}
#curator=${2}
#pmid=${3}
jarLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission
jvmparams='-Xms4096m -Xmx4096m'
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission/config
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/import-submission
source ${configlocation}/db-env


documentParameters="${jvmparams}"
echo "documentParameters is ${documentParameters}"

#if [ -d ${logslocation}/${submissionid} ]
#then
#        rm -rf ${logslocation}/${submissionid}
#fi
#mkdir -p $logslocation/$submissionid


export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PASSWORD}

#java -DentityExpansionLimit=100000000 -Dspring.profiles.active=cluster  -Dspring.datasource.username=${ORACLE_DB_USER} -Dspring.datasource.password=${ORACLE_DB_PWD} -Dsubmission.partition.size=${PARTITION_SIZE}  \
#    ${documentParameters} \
#    -jar ${jarLocation}/gwas-submission-import-pipeline.jar -s ${submissionid} -c ${curator} -p ${pmid}

java -DentityExpansionLimit=100000000 -Dspring.profiles.active=cluster  -Dspring.datasource.username=${ORACLE_DB_USER} -Dspring.datasource.password=${ORACLE_DB_PWD} -Dsubmission.partition.size=${PARTITION_SIZE}  \
    ${documentParameters} \
    -jar ${jarLocation}/gwas-submission-import-pipeline.jar

# Capture exit code:
exit $?
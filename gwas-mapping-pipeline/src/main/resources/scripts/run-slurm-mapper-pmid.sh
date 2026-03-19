#!/bin/bash
#SBATCH -t 02:00:00

#SBATCH --mem=4G

base=${0%/*}/;
scriptLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline-pmid
hashdir=${1}
asscnIds=${2}
executorPool=${3}
submissionId=${4}
mode=${5}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline-pmid/logs/bsub
mkdir -p $logslocation/$executorPool/$hashdir
#SBATCH --partition=short
#SBATCH -J "gwas-mapper"
echo "Bsub log dir is: ${logslocation}/${executorPool}/${hashdir}"

echo "sbatch -t 1:00:00 -mem=4G -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -J  \"gwas-mapper\" --partition=short ${scriptLocation}/map-association-pmid.sh  -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} ${submissionId} "

${scriptLocation}/map-association-pmid.sh -m ${mode} 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} ${submissionId}
exit $?
#!/bin/bash
#SBATCH -t 04:00:00

#SBATCH --mem=4G

base=${0%/*}/;
scriptLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/data-copy-service
hashdir=${1}
asscnIds=${2}
executorPool=${3}
mode=${4}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/data-copy-service/logs/bsub
mkdir -p $logslocation/$executorPool/$hashdir
#SBATCH --partition=short
#SBATCH -J "gwas-mapper"
echo "Bsub log dir is: ${logslocation}/${executorPool}/${hashdir}"

echo "sbatch -t 1:00:00 -mem=4G -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -J  \"gwas-mapper\" --partition=short ${scriptLocation}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} "

${scriptLocation}/copy-data-services.sh -m publication -p ${asscnIds} ${logslocation}/${executorPool}/${hashdir}
exit $?
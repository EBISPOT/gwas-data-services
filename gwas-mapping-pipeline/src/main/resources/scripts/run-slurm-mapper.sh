#!/bin/bash
#SBATCH -t 04:00:00

#SBATCH --mem=4G

base=${0%/*}/;
hashdir=${1}
asscnIds=${2}
executorPool=${3}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub
mkdir -p $logslocation/$executorPool/$hashdir
#SBATCH --output=${logslocation}/${executorPool}/${hashdir}/output.log
#SBATCH --error=${logslocation}/${executorPool}/${hashdir}/error.log
#SBATCH --partition=short
#SBATCH -J "gwas-mapper"
echo "Bsub log dir is: ${logslocation}/${executorPool}/${hashdir}"

echo "sbatch -t 1:00:00 -mem=4G -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -J  \"gwas-mapper\" --partition=short ${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} "

${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir}
exit $?
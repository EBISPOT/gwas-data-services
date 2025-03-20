#!/bin/bash
#SBATCH -t 06:00:00

#SBATCH --mem=4G

base=${0%/*}/;
scriptLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/parent-trait-mapper-executor
hashdir=${1}
efoIds=${2}
executorPool=${3}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/parent-trait-mapper-executor/logs/bsub
mkdir -p $logslocation/$executorPool/$hashdir
#SBATCH --partition=short
#SBATCH -J "gwas-trait-mapper-executor"
echo "Bsub log dir is: ${logslocation}/${executorPool}/${hashdir}"

echo "sbatch -t 1:00:00 -mem=4G -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -J  \"gwas-mapper\" --partition=short ${scriptLocation}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} "

${scriptLocation}/map-parent-traits.sh ${efoIds} ${logslocation}/${executorPool}/${hashdir}
exit $?
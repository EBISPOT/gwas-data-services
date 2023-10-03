#!/bin/sh

base=${0%/*}/;
hashdir=${1}
asscnIds=${2}
executorPool=${3}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub
mkdir -p $logslocation/$executorPool/$hashdir

echo "Bsub log dir is: ${logslocation}/${executorPool}/${hashdir}"

echo "bsub -M 4096 -R \"rusage[mem=4096]\" -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -g /gwas-mapper -K -q short \"${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir} \""

bsub -M 4096 -R "rusage[mem=4096]" -o ${logslocation}/${executorPool}/${hashdir}/output.log -e ${logslocation}/${executorPool}/${hashdir}/error.log -g /gwas-mapper -K -q short "${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds} ${logslocation}/${executorPool}/${hashdir}"
exit $?
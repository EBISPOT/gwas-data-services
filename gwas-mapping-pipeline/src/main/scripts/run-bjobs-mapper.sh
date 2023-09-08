#!/bin/sh

base=${0%/*}/;
hashdir=${1}
asscnIds=${2}
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub
mkdir -p $logslocation/$hashdir

echo "Bsub log dir is: ${logslocation}/${hashdir}"

echo "bsub -M 4096 -R \"rusage[mem=4096]\" -o ${logslocation}/${hashdir}/output.log -e ${logslocation}/${hashdir}/error.log -g /gwas-mapper -K -q short \"${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds}\""

bsub -M 4096 -R "rusage[mem=4096]" -o ${logslocation}/${hashdir}/output.log -e ${logslocation}/${hashdir}/error.log -g /gwas-mapper -K -q short "${base}/map-association.sh -m map-asscn-ids 40 ${asscnIds}"
exit $?
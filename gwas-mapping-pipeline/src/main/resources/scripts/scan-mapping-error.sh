#!/bin/sh


logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub

cd ${logslocation}

bsub -M 4096 -R "rusage[mem=4096]" -i  'find . -name "output.log" > ${logslocation}/outputFileList'
exit $?


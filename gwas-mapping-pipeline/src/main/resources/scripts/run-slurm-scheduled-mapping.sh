#!/bin/bash
#SBATCH -t 04:00:00

#SBATCH --mem=4G
#SBATCH --output=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-output.log
#SBATCH --error=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-error.log
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/mapping-pipeline.sh schedule
exit $?
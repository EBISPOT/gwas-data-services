#!/bin/bash
#SBATCH -t 192:00:00
#SBATCH --mem=4G
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/mapping-pipeline.sh full
exit $?
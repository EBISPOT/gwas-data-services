#!/bin/bash
#SBATCH -t 120:00:00
#SBATCH --mem=4G
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/parent-trait-mapper/parent-trait-mapper.sh
exit $?
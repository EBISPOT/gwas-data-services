#!/bin/bash
#SBATCH -t 24:00:00
#SBATCH --mem=4G
base=${0%/*}/;
mode=${1}
partition_size=${2}
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/parent-trait-mapper-executor/parent-trait-mapper.sh ${mode} ${partition_size}
exit $?
#!/bin/bash
#SBATCH -t 16:00:00
#SBATCH --mem=4G
#SBATCH -o /hps/nobackup/parkinso/spot/gwas/logs/sbatch/slurm-%j.out
#SBATCH -e /hps/nobackup/parkinso/spot/gwas/logs/sbatch/slurm-%j.err
base=${0%/*}/;
mode=${1}
echo "mode is ${mode}"
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline-pmid/mapping-pipeline-pmid.sh ${mode}
exit $?
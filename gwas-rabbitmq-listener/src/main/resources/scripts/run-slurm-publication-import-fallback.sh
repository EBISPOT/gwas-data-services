#!/bin/bash
#SBATCH -t 48:00:00
#SBATCH --mem=4G
#SBATCH --job-name=publication-import-fallback
#SBATCH -o /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-fallback-start.out
#SBATCH -e /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-fallback-start.err
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/gwas-rabbitmq-listener/publication-import-fallback.sh
exit $?
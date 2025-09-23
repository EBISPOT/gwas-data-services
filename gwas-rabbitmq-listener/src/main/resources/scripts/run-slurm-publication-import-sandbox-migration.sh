#!/bin/bash
#SBATCH -t 48:00:00
#SBATCH --mem=4G
#SBATCH --job-name=publication-import-sandbox-migration
#SBATCH -o /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-sandbox-migration-start.out
#SBATCH -e /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-sandbox-migration-start.err
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/gwas-rabbitmq-listener/publication-import-sandbox-migration.sh
exit $?
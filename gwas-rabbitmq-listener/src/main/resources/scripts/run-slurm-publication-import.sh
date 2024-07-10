#!/bin/bash
#SBATCH -t 48:00:00
#SBATCH --mem=4G
#SBATCH --job-name=publication-import
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/gwas-rabbitmq-listener/publication-import.sh
exit $?
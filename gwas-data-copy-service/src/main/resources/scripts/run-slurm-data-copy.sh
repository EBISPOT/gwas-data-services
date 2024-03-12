#!/bin/bash
#SBATCH -t 08:00:00
#SBATCH --mem=4G
base=${0%/*}/;
echo "base is ${base}"
/hps/software/users/parkinso/spot/gwas/prod/sw/data-copy-service/data-copy-service.sh publication
exit $?
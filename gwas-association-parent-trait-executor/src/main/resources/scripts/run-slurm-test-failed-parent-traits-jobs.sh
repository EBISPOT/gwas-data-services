#!/bin/bash
#SBATCH -t 00:10:00
#SBATCH --mem=4G

base=${0%/*}/;
scriptLocation=/hps/software/users/parkinso/spot/gwas/prod/sw/parent-trait-mapper-executor
${scriptLocation}/test-failed-parent-traits-jobs.sh
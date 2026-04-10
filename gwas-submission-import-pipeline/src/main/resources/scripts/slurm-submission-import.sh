#!/bin/bash
#SBATCH -t 10:00:00
#SBATCH --mem=8G
base=${0%/*}/;
#submissionid=${1}
#curator=${2}
#pmid=${3}
source /hps/software/users/parkinso/spot/gwas/anaconda3/bin/activate base;
conda activate gwas-utils;
module load nextflow-21.10.6-gcc-9.3.0-tkuemwd;
cd /hps/nobackup/parkinso/spot/gwas/logs/import-submission;
cp /hps/software/users/parkinso/spot/gwas/prod/sw/import-submission/config/nextflow.config .;

#/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission/import-submission.sh ${submissionid} ${curator} ${pmid}
/hps/software/users/parkinso/spot/gwas/prod/sw/import-submission/import-submission.sh
exit $?
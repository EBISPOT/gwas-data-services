#!/bin/bash
#SBATCH -t 00:10:00
#SBATCH --mem=4G
#SBATCH --job-name=publication-import
#SBATCH -o /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-stop.out
#SBATCH -e /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/rabbitmq-stop.err
echo "sending SIGTERM signal to Pub celery workers"
scancel --name=publication-import --signal=TERM --full
exit $?
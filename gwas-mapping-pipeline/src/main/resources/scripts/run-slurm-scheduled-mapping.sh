base=${0%/*}/;
echo "base is ${base}"

srun -t 4:00:00 -mem=4G -o /hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-output.log -e /hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-error.log  "/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/mapping-pipeline.sh schedule"
exit $?
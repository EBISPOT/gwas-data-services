base=${0%/*}/;
echo "base is ${base}"

bsub -M 4096 -R "rusage[mem=4096]" -o /hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-output.log -e /hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub/mapping-error.log  "/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/mapping-pipeline.sh schedule"
exit $?
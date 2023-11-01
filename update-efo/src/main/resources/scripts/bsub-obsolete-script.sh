#!/bin/sh

base=${0%/*}/;
profile=${1}
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";

bsub -M 4096 -R "rusage[mem=4096]" -o /hps/nobackup/parkinso/spot/gwas/logs/obsolete-efo/logs/bsub/obsolete-efo-output.log -e /hps/nobackup/parkinso/spot/gwas/logs/obsolete-efo/logs/bsub/obsolete-efo-error.log  "/hps/software/users/parkinso/spot/gwas/prod/sw/obsolete-efo/run-obsolete-efos.sh ${profile}"

# Capture exit code:
exit $?
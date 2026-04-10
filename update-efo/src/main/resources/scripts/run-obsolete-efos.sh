#!/bin/bash
#SBATCH -t 02:00:00
#SBATCH --mem=4G
#SBATCH -o /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/obsolete_efo.out
#SBATCH -e /hps/nobackup/parkinso/spot/gwas/logs/sbatch/prod/obsolete_efo.err

profile=${1}
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/config
source ${configlocation}/db-env

java -DentityExpansionLimit=100000000 \
  -Dspring.profiles.active=${profile} \
  -Dspring.data.mongodb.uri=${MONGO_DB_URI} \
  -Dspring.data.mongodb.database=${MONGO_DB_NAME} \
  -Dspring.datasource.url=${DB_URL} \
  -Dspring.datasource.username=${DB_USER} \
  -Dspring.datasource.password=${DB_PWD} \
  -Dspring.datasource.driver-class-name={DB_DRIVER} \
  $proxy_settings -Xms4096m -Xmx4096m -jar $base/update-efo.jar
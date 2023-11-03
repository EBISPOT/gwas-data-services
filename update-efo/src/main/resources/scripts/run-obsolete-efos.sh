#!/bin/sh

base=${0%/*}/;
profile=${1}
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/mapping-pipeline/config
source ${configlocation}/db-env

java -DentityExpansionLimit=100000000  -Dspring.profiles.active=${profile} -Dspring.data.mongodb.username=${MONGO_DB_USER} Dspring.data.mongodb.password=${MONGO_DB_PWD}  $proxy_settings -Xms4096m -Xmx4096m -jar $base/update-efo.jar

exit $?
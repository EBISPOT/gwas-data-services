#!/bin/sh

base=${0%/*}/;
logslocation=${5}
configlocation=/hps/software/users/parkinso/spot/gwas/prod/sw/data-copy-service/config
source ${configlocation}/db-env
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";

export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PWD}
echo "logslocation is :${logslocation}"
java -DentityExpansionLimit=100000000  -Dspring.profiles.active=cluster -Dlogging.file.path=${logslocation} -Dspring.datasource.username=${ORACLE_DB_USER} -Dspring.datasource.password=${ORACLE_DB_PWD}  $proxy_settings -Xms4096m -Xmx4096m -jar $base/gwas-copy-table-data.jar $@

exit $?
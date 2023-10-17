#!/bin/sh

base=${0%/*}/;
logslocation=${5}
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";

echo "logslocation is :${logslocation}"
java -DentityExpansionLimit=100000000  -Dspring.profiles.active=cluster -Dlogging.file.path=${logslocation}  $proxy_settings -Xms4096m -Xmx4096m -jar $base/gwas-mapping-service.jar $@

exit $?
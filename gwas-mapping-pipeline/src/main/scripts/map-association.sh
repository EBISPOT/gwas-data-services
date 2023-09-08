#!/bin/sh

base=${0%/*}/;
proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";

java -DentityExpansionLimit=100000000 -Dexecutor.thread-pool.count=5 -Dassociation.partition.size=2 -Dspring.profiles.active=cluster $proxy_settings -Xms4096m -Xmx4096m -jar $base/gwas-mapping-service.jar $@

exit $?
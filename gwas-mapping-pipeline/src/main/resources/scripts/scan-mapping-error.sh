#!/bin/sh


logslocation=/hps/nobackup/parkinso/spot/gwas/logs/mapping-pipeline/logs/bsub

#cd ${logslocation}
echo "loglocation is ${logslocation}"

find  ${logslocation} -name "output.log" > ${logslocation}/outputFileList
while read -r line; do
        filename="${line}"
        echo "File name to be analysed is ${filename}"
        grep "was not mapped due to error" ${filename} >> ${logslocation}/mapping-error-ids.log
done < ${logslocation}/outputFileList
exit $?
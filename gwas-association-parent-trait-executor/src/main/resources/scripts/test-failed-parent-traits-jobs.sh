#!/bin/sh

scriptDir=${0%/*}/;
logslocation=/hps/nobackup/parkinso/spot/gwas/logs/parent-trait-mapper-executor/logs/work/logs
errorfile=gwas-association-parent-trait-executor-msub-status.log


if [ -f ${logslocation}/${errorfile} ]
then
        if [ -s ${logslocation}/${errorfile} ]
        then
                echo "File ${logslocation}/${errorfile} is not empty"
                exit 1
        else
                echo "File ${logslocation}/${errorfile} is empty"
                exit 0
        fi
else
        echo "File ${logslocation}/${errorfile} does not exist"
        exit 0
fi
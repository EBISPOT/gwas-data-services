nextflow.enable.dsl=2

params.job_map_file = ''
params.log_path = ''

// ------------------------------------------------------------- //
// Run the solr indexer jobs                                     //
// ------------------------------------------------------------- //


process run_submission_import {
tag "$id"
memory { 8.GB * task.attempt }
maxRetries 3
errorStrategy { task.exitStatus in 1..255 ? 'retry' : 'terminate' }

input:
tuple val(id), val(cmd)

output:
stdout

"""
echo $id
$cmd
"""
}


workflow {
jobs = channel.fromPath("$params.job_map_file").splitCsv()
run_submission_import(jobs)
}
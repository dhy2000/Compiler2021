#!/bin/bash

# run this script at root dir of project: `scripts/submit.py`
# cg_submit.py needs a configuration file of JSON format, sample: https://github.com/dhy2000/CG-Submit/blob/master/cg_config_sample.json

scripts/zip.sh src/ && cd scripts/ && python -u cg_submit.py ../Compiler2021.zip ; cd ..



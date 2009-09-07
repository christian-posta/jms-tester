#!/bin/sh

cd `dirname $0`
SCRIPT_DIR=`pwd` 
ACTIVEMQ_HOME=$1

for brokerCfg in `ls *.xml` 
do
  CFG_FILE=xbean:file:$SCRIPT_DIR/$brokerCfg
  nohup $1/bin/activemq $CFG_FILE > $SCRIPT_DIR/$brokerCfg.out &
done


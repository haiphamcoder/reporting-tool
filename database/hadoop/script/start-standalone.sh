#!/bin/bash

# Format namenode if it doesn't exist
if [ ! -d "/hadoop/dfs/namenode/current" ]; then
    echo "Formatting namenode..."
    ${HADOOP_HOME}/bin/hdfs namenode -format
fi

# Start namenode
${HADOOP_HOME}/bin/hdfs namenode &

# Start datanode
${HADOOP_HOME}/bin/hdfs datanode &

tail -f /dev/null

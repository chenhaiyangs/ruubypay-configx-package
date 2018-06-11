#!/bin/sh
JAR_NAME=configx-web.jar
#root dir
DIR="$( cd "$( dirname "$0"  )" && pwd  )"
if [ "$1" ]
then
	JAR_NAME="$1"
	if [ ! -f "$1" ]; then
		echo "${DIR}/$1 is not exists ! please check your input !"
		exit 0
	fi
fi
#log file
CURRENT_LOG=${DIR}/logs/configx-web.log
#server_pid
SERVER_HAS_PID=$(ps aux|grep ${JAR_NAME} |grep -v grep|grep -v stop.sh|awk '{print $2}')

if [ -n "${SERVER_HAS_PID}" ]; then
	echo "beginning to shutdown ${JAR_NAME}......" 
	echo "shuting......"
	kill -15 ${SERVER_HAS_PID}
	sleep 2
	tail -n6 ${CURRENT_LOG}
	echo "ok"
else 
	echo "${JAR_NAME} is not runnging......"
fi
#!/bin/bash

if [ "$1" = "run" ] ; then
export TZ="${TZ:-America/New_York}"
export HOST_IP="${HOST_IP:-$(host $HOST | awk '/has address/ { print $4 ; exit }')}"
export JENKINS_EXECUTORS=${JENKINS_EXECUTORS:-10}
export JENKINS_QUIET_PERIOD=${JENKINS_QUIET_PERIOD:-0}
export JENKINS_SLAVE_AGENT_PORT=${JENKINS_SLAVE_AGENT_PORT:-50000}

find /usr/share/jenkins/ref -type f -exec bash -c ". /usr/local/bin/jenkins-support; copy_reference_file '{}'" \;
find /usr/share/jenkins \( -not -user jenkins -a -not -type l \) -exec chown jenkins:jenkins '{}' \;
find /jenkins \( -not -user jenkins -a -not -type l \) -exec chown jenkins:jenkins '{}' \;

exec gosu jenkins:jenkins \
	java $JAVA_OPTS \
        -XX:NativeMemoryTracking=summary \
        -Dorg.apache.commons.jelly.tags.fmt.timeZone=$TZ \
        -Duser.timezone=$TZ \
        -Dhudson.TcpSlaveAgentListener.hostName=$HOST_IP \
        -Dhudson.TcpSlaveAgentListener.port=$JENKINS_SLAVE_AGENT_PORT \
        -Djenkins.install.runSetupWizard=false \
	-Dhudson.security.csrf.requestfield=Jenkins-Crumb \
        -jar /usr/share/jenkins/jenkins.war $JENKINS_OPTS "$@"
exit $?
fi

exec "$@"

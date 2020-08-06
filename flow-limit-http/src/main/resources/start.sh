#!/bin/bash

logPATH="/home/logs/flow-limit-http"
LOG4M="log4m.properties"

exec java -cp .:conf/*:lib/* -Dmomo.log.name=$LOG4M -Xmx2000m -Xms2000m -verbose:gc -Xloggc:$logPATH/gc.log -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSCompactAtFullCollection -XX:MaxTenuringThreshold=10 -XX:-UseAdaptiveSizePolicy -XX:PermSize=10M -XX:MaxPermSize=50M -XX:SurvivorRatio=3  -XX:NewRatio=2 -XX:+PrintGCDateStamps -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails com.immomo.mts.flow.limit.http.Application >>$logPATH/stdout.log 2>&1
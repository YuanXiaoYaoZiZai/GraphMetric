#!/bin/bash

CUR_DIR=$(cd "$(dirname "$0")"; pwd)
PID_FILE="pid.txt"
ulimit -n 65535

#load config
CTRL_CFG=$CUR_DIR/control.config
if [ ! -f "$CTRL_CFG" ] ; then
    echo "Control config file[$CTRL_CFG] not exist!";
    exit 0
else
    source $CTRL_CFG
fi

# default 600 second
if [ -z "${MON_TIME}" ]; then
    MON_TIME=600
fi

if [ -z "${APP_DIR}" ]; then
    APP_PATH=$CUR_DIR/$APP_NAME
else
    APP_PATH=$APP_DIR/$APP_NAME
fi

MON_FILE_PATH=$CUR_DIR/$MON_FILE
TODAY=`date "+%Y-%m-%d"`
MON_LOG="$CUR_DIR/logs/monitor-$TODAY.log"

function log(){
    ct=`date "+%Y-%m-%d %H:%M:%S"`
    echo "$ct $*" | tee -a $MON_LOG
}

source ~/.bashrc
#use default java sdk
#if [ -z "$JAVA_HOME" ] ; then
#    log "JAVA_HOME is not set"
#    exit 1
#fi

JAVA=java
ARGS=$1
CMD=


## show command help
function showHelp()
{
    echo "Usage control.sh [start|stop|restart|status]"
    echo -e "\t start  : Start service"
    echo -e "\t stop   : Stop service"
    echo -e "\t restart: Restart service"
    echo -e "\t status : Show service status"
}

## get current process id
function currentPid(){
        #ps -ef|grep java | grep $APP_NAME |grep -v grep|awk '{print $2}'
        echo $(<"$PID_FILE")
}
## check process id
function checkPid(){
    cpid=`currentPid`
        ps -ef|grep java | grep ${cpid} |grep -v grep|awk '{print $2}'
}

## check service status
function checkActive(){
    cpid=`checkPid`
    if [ -z "${cpid}" ] ; then
            log "Service not exist."
        return 1
    fi
   
    if [ -f "${MON_FILE_PATH}" ] ; then
        lmt=`stat -c %Y ${MON_FILE_PATH}`
        ct=`date +%s`

            dt=`expr $ct - $lmt`
        if [ $dt -gt $MON_TIME ] ; then
            log "File[$MON_FILE_PATH] not change from $lmt"
                return 1
        fi
    fi

    return 0
}

## start service
function startService()
{
        cpid=`checkPid`
        if [ ${cpid} ] ; then
                log "Service[$APP_NAME] already running,pid($cpid)."
        else
                log "Start Service:$APP_NAME"
                log "JAVA_OPTS:$JAVA_OPTS"
                line=`lscpu | grep "NUMA node" | wc -l`
                log "NUMA node cnt: ${line}"
                if  [ ${line} -ne 0 ] || [ -z "$APP_CONF" ]; then				   
                    CMD="numactl --interleave=all $JAVA -jar $JAVA_OPTS $APP_PATH"
                    log "Run cmd:[$CMD]"
                    nohup $CMD >/dev/null 2>&1 &
                    #nohup $CMD >>$CDIR/123.log 2>&1 &
                    echo $! > $PID_FILE
                        log "Start service with numa done."
              
                else
                        CMD="$JAVA -jar $JAVA_OPTS $APP_PATH --spring.config.location=$APP_CONF"
                        log "Run cmd:[$CMD]"
            nohup $CMD >/dev/null 2>&1 &
            echo $! > $PID_FILE
                        log "Start service with config done."
                fi
        fi
}

#stop service
function stopService()
{
        cpid=`checkPid`
        if [ ${cpid} ]; then
                log "Stop Service[$APP_NAME]($cpid)"
                #kill pid and children pids
                #ref:http://stackoverflow.com/questions/392022/best-way-to-kill-all-child-processes
        #kill -9 $cpid
        pkill -9 -P $cpid
        sleep 1
        log "Kill [$APP_NAME] Children by Parent PID[$cpid]"
        kill -9 $cpid
        sleep 1
        log "Kill [$APP_NAME] PID[$cpid]"
        else
                log "Stop Service[$APP_NAME] is not running."
        fi
}

#restart service
function restartService()
{
        log "Restart service[$APP_NAME]"
        stopService
        sleep 3
        startService
}

#show service status
function showStatus()
{
        echo "Service[$APP_NAME] status:"
        echo "-------------------------------------------------------------"
        cpid=`currentPid`
        ps -ef|grep java | grep ${cpid} |grep -v grep

        if [ ${cpid} ]; then
        ps -f --no-headers --ppid ${cpid}
        fi

        echo "-------------------------------------------------------------"
}

#
function monitorService(){
    checkActive
    activeStatus=$?

    if [ $activeStatus -eq 1 ]; then
        log "Service state is abnormal,restart service."
        restartService
    else
        log "Service state is normal."
    fi

}



## check args not empty
if [ -z "$ARGS" ]; then
        showHelp
        exit 0
fi

log "==========================================================="
log "Cmd        : $0 $*"
log "Config     : $CTRL_CFG"
log "Dir        : $CUR_DIR"
log "App        : $APP_PATH"
log "Monitor    : $MON_FILE($MON_TIME)"
echo ""

## switch control command
case "$ARGS" in
        start) startService && sleep 3 && showStatus ;;
        stop)  stopService && showStatus  ;;
        restart) restartService && showStatus ;;
        status)  showStatus ;;
        monitor) monitorService;;
        *)       showHelp ;;
esac

exit 0
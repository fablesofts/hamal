#!/bin/bash 

current_path=`pwd`
case "`uname`" in
    Linux)
		bin_abs_path=$(readlink -f $(dirname $0))
		;;
	*)
		bin_abs_path=`cd $(dirname $0); pwd`
		;;
esac
type=-i
base=${bin_abs_path}/..
logback_configurationFile=$base/conf/logback.xml
export LANG=en_US.UTF-8

if [ -f $base/bin/hamal.channel.pid ] ; then
	echo "found hamal.channel.pid , Please run stop.sh first ,then startup.sh" 2>&2
    exit 1
fi

if [ ! -d $base/logs/channel ] ; then 
	mkdir -p $base/logs/channel
fi

## set java path
if [ -z "$JAVA" ] ; then
  JAVA=$(which java)
fi

case "$#" 
in
0 ) 
	;;
1 )	
	var=$*
	if [ "$var" != "-i" && "$var" != "-o" ] 
	then 
		echo "THE PARAMETER IS NOT CORRECT.PLEASE CHECK AGAIN."
		echo "startup.sh [debug] [port] -i || startup.sh [debug] [port] -o"
        exit
	else
		type=$var
		echo $type
	fi;;
3 )	
	var1=$1
	var2=$2
	var3=$3
	if [ "$var3" != "-i" && "$var3" != "-o" ] ; then
		echo "THE PARAMETER IS NOT CORRECT.PLEASE CHECK AGAIN."
		echo "startup.sh [debug] [port] -i || startup.sh [debug] [port] -o"
        exit
	else
		type=$3 
		if [ "$1" = "debug" ]; then
			DEBUG_PORT=$2
			DEBUG_SUSPEND="n"
			JAVA_DEBUG_OPT="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=$DEBUG_SUSPEND"
		else
			echo "THE PARAMETER IS NOT CORRECT.PLEASE CHECK AGAIN."
			echo "startup.sh [debug] [port] -i || startup.sh [debug] [port] -o"
	        exit
		fi
     fi;;
* )
	echo "THE PARAMETERS MUST BE THREE OR ONE OR ZERO.PLEASE CHECK AGAIN."
	exit;;
esac

str=`file $JAVA_HOME/bin/java | grep 64-bit`
if [ -n "$str" ]; then
	JAVA_OPTS="-server -Xms2048m -Xmx3072m -Xmn1024m -XX:SurvivorRatio=2 -XX:PermSize=96m -XX:MaxPermSize=256m -Xss256k -XX:-UseAdaptiveSizePolicy -XX:MaxTenuringThreshold=15 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError"
else
	JAVA_OPTS="-server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:MaxPermSize=128m "
fi

JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
HAMAL_OPTS="-DappName=hamal-channel"

if [ -e $logback_configurationFile ]
then 
	for i in $base/lib/*;
	do CLASSPATH=$i:"$CLASSPATH";
	done
	CLASSPATH="$base/conf:$CLASSPATH";
 
	echo LOG CONFIGURATION : $logback_configurationFile
	echo CLASSPATH :$CLASSPATH

  echo "cd to $bin_abs_path for workaround relative path"
  cd $bin_abs_path

	$JAVA $JAVA_OPTS $JAVA_DEBUG_OPT $HAMAL_OPTS -classpath .:$CLASSPATH com.fable.hamal.proxy.ProxyServer $type -ain -aout 1>>$base/logs/channel/channel.log 2>&1 &
	echo $! > $base/bin/hamal.channel.pid 

  echo "cd to $current_path for continue"
  cd $current_path
else 
	echo "log configration file($logback_configurationFile) is not exist,please create then first!"
fi

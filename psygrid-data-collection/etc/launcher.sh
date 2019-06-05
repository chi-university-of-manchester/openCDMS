#!/bin/sh

## resolve links - $0 may be a symlink
PRG="$0"
progname=`basename "$0"`
saveddir=`pwd`

# need this for relative symlinks
cd `dirname "$PRG"`
  
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
	PRG="$link"
    else
	PRG=`dirname "$PRG"`"/$link"
    fi
done
  
#HOME=`dirname "$PRG"`

cd "$saveddir"

# make it fully qualified
HOME=`cd "$HOME" && pwd`

CLASSP=

for i in $saveddir/lib/*.jar
do
  CLASSP=$CLASSP:$i
done
for i in $saveddir/lib/linux/*.jar
do
  CLASSP=$CLASSP:$i
done
echo $CLASSP

java -classpath $CLASSP -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger org.psygrid.collection.entry.Launcher


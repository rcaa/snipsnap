#! /bin/sh
# $Id$
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  echo "A Java SDK of at least version 1.3 is required!"
  exit
fi

if [ ! -f $base/$jar/snipsnap.jar ]; then
  echo "$jar/snipsnap.jar missing, please compile application first"
  exit
fi

# put classpath together
CLASSPATH=$base/lib/mckoidb.jar:$base/lib/jdbcpool.jar:$base/lib/lucene-1.2.jar:$base/lib/org.apache.crimson.jar:$base/lib/radeox.jar:$base/lib/jdom-b8.jar:$base/lib/javax.servlet.jar

$JAVA_HOME/bin/java -cp $CLASSPATH:$base/lib/snipsnap-utils.jar org.snipsnap.util.DBImport $*

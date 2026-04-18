#!/bin/sh

##############################################################################
##
##  Gradle start up script for UN*X
##

APP_HOME=$(dirname "$(readlink -f "$0")")
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Resolve links: $0 may be a softlink
while [ -h "$APP_HOME/gradlew" ] ; do
  ls=`ls -ld "$APP_HOME/gradlew"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    APP_HOME=`dirname "$link"`
  else
    APP_HOME=`cd "$APP_HOME" && cd $(dirname "$link") && pwd`
  fi
done

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx2048m -Dfile.encoding=UTF-8"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
case "`uname`" in
  CYGWIN*)
    cygwin=true
    ;;
  Darwin*)
    darwin=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin ; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=\$APP_NAME\" \"-Xdock:icon=\$APP_HOME/media/gradle.icns\""
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"


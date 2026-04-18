#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##


APP_HOME=$(dirname "$0")
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx2048m" "-https.protocols=TLSv1.2,TLSv1.3" "-Dfile.encoding=UTF-8"'

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
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MSYS* | MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
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
if [ "$darwin" = "true" ]; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" -o "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d \|  grep ^/ \| sed -e 's[^/][]' -e 's[/]$[]'`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        SEP+="
"
        ROOTDIRS+="$dir"
    done
    OURCYGPATTERN="(^($ROOTDIRS):)"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN+="|($GRADLE_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"\|"${SEP}"$OURCYGPATTERN:`
        CHECK2=`echo "$CHECK" | tr -d '\\r' | tr -d '\\n' | sed -e 's/ /###/g' | grep -E "$OURCYGPATTERN" \| head -1`
        if [ "${CHECK2}" != "" ] ; then
            printf 'Arg %d is "%s"\\\\n' $i "$CHECK" >&2
            eval `echo "$CHECK" | sed -e "s/ /###/g" | sed \
                -e "s/^\\($OURCYGPATTERN):/ /" \
                -e "s/^\\(-D\\)/ /" \
                -e "s/^\\(-XX:\\)/ /" \
                -e "s/^\\(.[^-]\\)/ /" \
                -e "s/###/ /g" | tr -d '\\n' | sed -e "s%###% %g" \
                \| sed -e 's@ @\\ @g'`
            i=$((i+1))
        else
            printf "Arg %d is \"%s\"\\\\n" $i "$arg" >&2
            eval "set arg$i \\\"\\\$(printf '%s' '$arg')\\\""
            i=$((i+1))
        fi
    done
fi

saveIFS=$IFS
IFS=$'\n'

scriptArgs=()
case ${OS} in
  darwin|freebsd)
    scriptArgs=( "-u" "-f" )
    ;;
  *)
    scriptArgs=( "-f" )
    ;;
esac

exec "$JAVACMD" "${JAVA_OPTS:-$DEFAULT_JVM_OPTS}" "${GRADLE_OPTS:-}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"


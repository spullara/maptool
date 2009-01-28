#!/bin/bash

VERS=maptool*.jar
MAXMEMSZ="768m"		# The 'm' suffix means megabytes

MINMEMSZ="32m"
STACKSZ="2m"

APPDOCKNAME="-Xdock:name=MapTool"
APPDOCKICON="-Xdock:icon=RPTools_Map_Logo.png"

ALL_OPTS="-Xmx$MAXMEMSZ -Xms$MINMEMSZ -Xss$STACKSZ $APPDOCKNAME $APPDOCKICON"

# Figure out how many JARs are in the current directory.  If there's
# more than one, give the user a menu and let them choose one.
count=0
for jar in $VERS
do
    let count=count+1
    MAPTOOL="$jar"
done

# If there was only one, MAPTOOL is the expanded name.
# If there were more than one, we'll let the user decide which to use.
if ((count > 1)); then
    MAPTOOL=""
    PS3="Type the number of your choice and press <Enter>.
Or use Ctrl-C to terminate: "
    IFS=""	# Turn off word breaks on whitespace
    select jar in $VERS
    do
	if [[ "$jar" == "" ]]; then jar="$REPLY"; fi
	# The user's selection doesn't match any of the choices!
	# Maybe they entered a pathname?
	if [[ -e "$jar" ]]; then
	    # Yep, they gave us a filename.  We'll use it.
	    MAPTOOL="$jar"
	    break
	fi
	echo 1>&2 "" # Blank line
	echo 1>&2 "Error: Invalid input  Try again."
    done
    if [[ "$MAPTOOL" == "" ]]; then exit 1; fi	# Ctrl-D at the select prompt
    echo 1>&2 "" # Blank line
fi

echo 1>&2 "Executing $MAPTOOL ..."
java $ALL_OPTS -jar "$MAPTOOL" run
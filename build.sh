#!/bin/sh

if [ -z $ANT_HOME ]; then
  echo "Please set the ANT_HOME variable."
  exit 1
fi

if [ -z $JAVA_HOME ]; then
  echo "Please set the JAVA_HOME variable."
  exit 1
fi

export PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH
ant $*

#!/bin/bash
# author Steffen Dienst (sdienst@informatik.uni-leipzig.de)
#
# Gnu Public License v2, see license text at http://www.gnu.org/licenses/gpl.html
#
if [ -r "$JAVA_HOME"/bin/java ]; then
	java -jar elatexam-embedded-1.0.0-SNAPSHOT.jar
else
	echo "Please install the Java Development Kit (at least version 1.6.0)"
	echo "and set the environment variable JAVA_HOME to the installation directory."
	echo "Download url: http://www.oracle.com/technetwork/java/javase/downloads/index.html"
	exit 1
fi 
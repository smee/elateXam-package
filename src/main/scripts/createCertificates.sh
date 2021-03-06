#!/bin/bash
# author Steffen Dienst (sdienst@informatik.uni-leipzig.de)
#
# Gnu Public License v2, see license text at http://www.gnu.org/licenses/gpl.html
#
# Create a new jks keystore with a self signed certificate as well as a PKCS12 keypair. Both
# files can be used for implementing SSL in tomcat 5.5 including client authentication.
if [ -r "$JAVA_HOME"/bin/keytool ]; then 
	commonname="localhost"
	echo -n "Please enter the domainname of the elateXam server [" $commonname "]: "
	read commonname
	ownername="free form name of the institution"
	echo -n "Please enter the name of your organization [" $ownername "]: "
	read ownername
	location="Unknwown"
	echo -n "Please enter your location/city: [" $location "]: "
	read location
	password="password"
	echo -n "Please enter the password for all keys (length>=6):"
	read -s password
	echo ""
	
	# use default values for empty variables
	: ${commonname:="localhost"}
	: ${ownername:="Name of the organization"}
	: ${password:="password"}
	: ${location:="Unknown"}
	
	serveralias="tomcat"
	clientalias="clientCert"
	
	echo "Generating new server certificate key..."
	"$JAVA_HOME"/bin/keytool -genkeypair -alias ${serveralias} -keyalg RSA -validity 1000 -keystore server.keystore -dname cn="${commonname}",o="${ownername}",l="${location}" -keypass ${password} -storepass ${password}
	
	# wait for app. 1 second to prevent generation of two certificates with the same serial number
	sleep 1
	
	echo "Generating new client certificate key..."
	"$JAVA_HOME"/bin/keytool -genkeypair -alias ${clientalias} -keyalg RSA -validity 1000 -storetype pkcs12 -keystore clientcertificate.p12 -dname cn="${commonname}",o="${ownername}",l="${location}"  -storepass ${password} -keypass ${password}
	
	# export certificates
	echo "Importing public client certificate into server keystore..."
	"$JAVA_HOME"/bin/keytool -exportcert -alias ${clientalias} -keystore clientcertificate.p12 -storetype pkcs12 -storepass ${password} -file client-public.cer
	"$JAVA_HOME"/bin/keytool -importcert -v -alias ${clientalias} -file client-public.cer -keystore server.keystore -storepass ${password} -noprompt -trustcacerts
	
	rm client-public.cer
	"$JAVA_HOME"/bin/keytool -list -v -keystore server.keystore -storepass ${password}
	echo "Please import clientcertificate.p12 into your browser, use server.keystore as keystore/truststore in tomcat 5.5."
	echo "For details on configuring tomcat please refer to http://tomcat.apache.org/tomcat-5.5-doc/ssl-howto.html"
	echo "Done!"

  else
	echo "Please install the current Java Development Kit (JDK), version 1.6.0 or newer."
	echo "The download can be found at http://www.oracle.com/technetwork/java/javase/downloads/index.html"
	echo ""
	echo "JAVA_HOME should point to the installation directory!"
    exit 1
  fi


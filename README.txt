elateXam - eAssessment server
=============================

This package contains the complete elateXam server. It provides support for execution, correction and inspection
of exams.

It was developed at the University of Leipzig.

This server gets configured by the settings in sslsettings.properties. Per default the server runs an unencrypted
http connector at port 8080. For securing the server please create a java keystore that contains a valid certificate 
(including a RSA keypair) as well as at least one public key (for client authentication).
Use the provided scripts for generating self-signed certificates for test setups. Please change the password setting
accordingly.

The elateXam server can be accessed with any browser at http://localhost:8080/examServer or https://localhost:8443/examServer
 
elateXam - eAssessment server
=============================

This package contains the complete elateXam server. It provides support for execution, correction and inspection
of exams.

It was developed at the University of Leipzig.


Configuration:
==============
This server gets configured by the settings in sslsettings.properties. Per default the server runs an unencrypted
http connector at port 8080. For securing the server please create a java keystore that contains a valid certificate 
(including a RSA keypair) as well as at least one public key (for client authentication).
Please use the provided scripts (createCertificates.(sh|bat)) for generating self-signed certificates for test setups. Please change the password setting
accordingly.

All application data gets written to a directory located at {user.home}/ExamServerRepository_{name} where 
name is either "webapp" or the value of the variable "elatexam.name" in the configuration file 
sslsettings.properties. This is especially useful if you would like to run multiple instances of elateXam 
in parallel. In that case you would need to set the variable to a unique name before starting each instance. 


Running:
=========
There are two modes: Test run with an unsecured http connector OR completely secure SSL communication with 
client certificates (unsecured access only allowed from localhost).

Start the server using one of the 'runElateXam.(sh|bat)' scripts.

The elateXam server can be accessed with any browser at http://localhost:8080/examServer (only from localhost!) or https://localhost:8443/examServer
 
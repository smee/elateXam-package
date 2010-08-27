@echo off
rem author Steffen Dienst (sdienst@informatik.uni-leipzig.de)
rem
rem Gnu Public License v2, see license text at http://www.gnu.org/licenses/gpl.html

if exist "%JAVA_HOME%\bin\java.exe" goto javaAvailable
echo Please install the current Java Development Kit (JDK), version 1.6.0 or newer.
echo The download can be found at http://www.oracle.com/technetwork/java/javase/downloads/index.html
echo.
echo JAVA_HOME should point to the installation directory!
goto endScript


:javaAvailable

%JAVA_HOME%\bin\java.exe -jar elatexam-embedded-1.0.0-SNAPSHOT.jar

:endScript
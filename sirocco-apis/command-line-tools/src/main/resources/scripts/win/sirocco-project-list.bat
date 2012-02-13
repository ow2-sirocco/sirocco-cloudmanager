@REM
@REM
@REM SIROCCO
@REM Copyright (C) 2011 France Telecom
@REM Contact: sirocco@ow2.org
@REM
@REM This library is free software; you can redistribute it and/or
@REM modify it under the terms of the GNU Lesser General Public
@REM License as published by the Free Software Foundation; either
@REM version 2.1 of the License, or any later version.
@REM
@REM This library is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
@REM Lesser General Public License for more details.
@REM
@REM You should have received a copy of the GNU Lesser General Public
@REM License along with this library; if not, write to the Free Software
@REM Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
@REM USA
@REM
@REM  $Id: sirocco-project-list.bat 36 2011-06-20 14:38:41Z dangtran $
@REM
@REM

ECHO OFF

rem #!/bin/sh

rem cygwin=false;
rem case "`uname`" in
rem  CYGWIN*) cygwin=true;
rem esac


rem if [ -z "$SIROCCO_TOOLS_HOME" ];  then
rem  echo "Please set SIROCCO_TOOLS_HOME"
rem  exit 1
rem fi

rem echo ----------

if not exist %SIROCCO_TOOLS_HOME% goto label
rem then commands here

set PROPERTIES_FILE=%SIROCCO_TOOLS_HOME%\etc\sirocco_tools.properties
set LOGGING_CONFIG_FILE=%SIROCCO_TOOLS_HOME%\etc\logging.properties

rem java -cp %SIROCCO_TOOLS_HOME%\lib -Ddefault.properties=%PROPERTIES_FILE% -Djava.util.logging.config.file=%LOGGING_CONFIG_FILE% org.ow2.sirocco.cloudmanager.api.tools.ListImagesClient

rem -cp %SIROCCO_TOOLS_HOME%\lib
rem -jar sirocco-cloudmanager-api-tools-3.0.jar

java -cp %SIROCCO_TOOLS_HOME%\lib\sirocco-cloudmanager-api-tools-3.0.jar;%SIROCCO_TOOLS_HOME%\lib\jcommander-1.17.jar;%SIROCCO_TOOLS_HOME%\lib\cxf-bundle-jaxrs-2.3.3.jar;%SIROCCO_TOOLS_HOME%\lib\jsr311-api-1.1.1.jar;%SIROCCO_TOOLS_HOME%\lib\sirocco-cloudmanager-api-spec-3.0.jar;%SIROCCO_TOOLS_HOME%\lib\wsdl4j-1.6.2.jar -Ddefault.properties=%PROPERTIES_FILE% -Djava.util.logging.config.file=%LOGGING_CONFIG_FILE% org.ow2.sirocco.cloudmanager.api.tools.ListProjectsClient

rem java -cp %SIROCCO_TOOLS_HOME%\lib\* -Ddefault.properties=%PROPERTIES_FILE% -Djava.util.logging.config.file=%LOGGING_CONFIG_FILE% org.ow2.sirocco.cloudmanager.api.tools.ListImagesClient

goto :eof

:label
rem else conditions here
echo "You must set SIROCCO_TOOLS_HOME"

rem PROPERTIES_FILE=$SIROCCO_TOOLS_HOME/etc/sirocco_tools.properties
rem LOGGING_CONFIG_FILE=$SIROCCO_TOOLS_HOME/etc/logging.properties

rem CLASSPATH="$SIROCCO_TOOLS_HOME/lib/*"

rem if $cygwin; then
rem   CLASSPATH=`cygpath --path -w "$CLASSPATH"`
rem   CLASSPATH=";$CLASSPATH"
rem   PROPERTIES_FILE=`cygpath -w $PROPERTIES_FILE`
rem   LOGGING_CONFIG_FILE=`cygpath  -w $LOGGING_CONFIG_FILE`
rem fi

rem java -cp "$CLASSPATH" -Ddefault.properties=$PROPERTIES_FILE -Djava.util.logging.config.file=$LOGGING_CONFIG_FILE org.ow2.sirocco.cloudmanager.api.tools.ListImagesClient $@

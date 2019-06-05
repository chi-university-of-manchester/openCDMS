@echo off

setlocal ENABLEDELAYEDEXPANSION
set HOME=%cd%
set LIB=%HOME%\lib
set LIB_WIN=%HOME%\lib\win
set CLASSP=
cd %LIB%
for %%f in (*.jar) do set CLASSP=%LIB%\%%f;!CLASSP!
cd ..
cd %LIB_WIN%
for %%f in (*.jar) do set CLASSP=%LIB_WIN%\%%f;!CLASSP!
cd ..\..
java -classpath "%CLASSP%" -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger org.psygrid.collection.entry.Launcher
endlocal




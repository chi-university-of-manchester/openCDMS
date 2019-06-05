@echo off

setlocal ENABLEDELAYEDEXPANSION
set HOME=%cd%
set LIB=%HOME%\lib
set CLASSP=
cd %LIB%
for %%f in (*.jar) do set CLASSP=%LIB%\%%f;!CLASSP!
cd ..
echo %CLASSP%
java -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger -Xmx256m -classpath "%CLASSP%" org.psygrid.datasetdesigner.ui.Main
endlocal




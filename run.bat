@echo off
REM Bien dich va chay So Thu Chi (Windows)
chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\sothuchi\*.java src\sothuchi\model\*.java src\sothuchi\data\*.java src\sothuchi\service\*.java src\sothuchi\view\*.java src\sothuchi\util\*.java
if errorlevel 1 (
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp bin sothuchi.Main
pause

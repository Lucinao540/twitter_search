@echo off & setlocal enabledelayedexpansion

title twitter

java -classpath %class_path% -Xmx512m  -jar twitter__fat.jar
pause
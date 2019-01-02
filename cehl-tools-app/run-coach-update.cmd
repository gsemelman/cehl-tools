@ECHO OFF
CLS

java -cp lib/*;config/ org.springframework.boot.loader.PropertiesLauncher -coachUpdate input/coachUpdate.csv

PAUSE
EXIT
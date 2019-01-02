@ECHO OFF
CLS

REM java -jar lib/cehl-tools-1.0-SNAPSHOT.jar -unassignedCleanup input/unassignedCleanup.csv --spring.config.location=config/
java -cp lib/*;config/ org.springframework.boot.loader.PropertiesLauncher -unassignedCleanup input/unassignedCleanup.csv

PAUSE
EXIT
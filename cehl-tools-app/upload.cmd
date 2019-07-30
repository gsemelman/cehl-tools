@ECHO OFF
CLS

java -cp lib/*;config/ org.springframework.boot.loader.PropertiesLauncher -upload

PAUSE
EXIT
@echo .
@echo .
@echo .
@echo .
@echo .
@echo .
rem TODO: check if classes directory exists rather than always trying to create it
mkdir classes
javac -source 1.4 -Xlint:unchecked -d classes\ src\*.java

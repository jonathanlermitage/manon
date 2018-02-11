@echo off

if [%1] == [-h] ( 
  echo  t: test
  echo  b: compile
  echo  c: clean
  echo  p: package
  echo  w $V: set or upgrade Maven wrapper to version $V
  echo  cv: check plugins and dependencies versions
  echo  up: update plugins and dependencies versions
  echo  dt: show dependencies tree
)

if [%1] == [t] ( 
  echo mvnw clean test
  mvnw clean test
)
if [%1] == [b] (
  echo mvnw clean compile -DskipTests -T1
  mvnw clean compile -DskipTests -T1
)
if [%1] == [c] (
  echo mvnw clean
  mvnw clean 
)
if [%1] == [p] ( 
  echo mvnw clean package -DskipTests -T1
  mvnw clean package -DskipTests -T1 
)
if [%1] == [w] ( 
  echo mvn -N io.takari:maven:wrapper -Dmaven=%2
  mvn -N io.takari:maven:wrapper -Dmaven=%2
)
if [%1] == [cv] (
  echo mvnw versions:display-plugin-updates versions:display-dependency-updates -U
  mvnw versions:display-plugin-updates versions:display-dependency-updates -U
)
if [%1] == [uv] (
  echo mvnw versions:update-properties -U
  mvnw versions:update-properties -U
)
if [%1] == [dt] (
  echo mvnw dependency:tree
  mvnw dependency:tree
)

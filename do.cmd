@echo off

if [%1] == [help] (
  echo  t: test
  echo  tc: test and generate coverage data
  echo  sc: compute and upload Sonar analysis to SonarCloud
  echo  b: compile
  echo  c: clean
  echo  p: package
  echo  w $V: set or upgrade Maven wrapper to version $V
  echo  cv: check plugins and dependencies versions
  echo  uv: update plugins and dependencies versions
  echo  dt: show dependencies tree
)

if [%1] == [t] ( 
  echo mvnw clean test
  mvnw clean test
)
if [%1] == [tc] (
  echo mvnw clean test -Pcoverage
  mvnw clean test -Pcoverage
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
if [%1] == [sc] (
  echo mvnw sonar:sonar -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
  mvnw sonar:sonar -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
)

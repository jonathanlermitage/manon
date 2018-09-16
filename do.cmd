@echo off

if [%1] == [help] (
  echo  t:      test
  echo  tc:     test and generate coverage data
  echo  sc:     compute and upload Sonar analysis to SonarCloud
  echo  tsc:    similar to "do tc" then "do sc"
  echo  b:      compile
  echo  c:      clean
  echo  p:      package
  echo  rd:     package and run application with dev profile
  echo  w $V:   set or upgrade Maven wrapper to version $V
  echo  cv:     check plugins and dependencies versions
  echo  uv:     update plugins and dependencies versions
  echo  dt:     show dependencies tree
  echo  jib:    build Docker image to a Docker daemon
  echo  jibtar: build and save Docker image to a tarball
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
if [%1] == [rd] (
  echo build project: mvnw clean package -DskipTests -T1
  mvnw clean package -DskipTests -T1
  echo move to app: cd target
  cd target
  echo run app in dev mode: java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
  java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
  echo application exit
  echo return to root: cd ..
  cd ..
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
if [%1] == [tsc] (
  echo mvnw clean test sonar:sonar -Pcoverage -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
  mvnw clean test sonar:sonar -Pcoverage -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
)
if [%1] == [jib] (
  echo mvnw clean compile jib:dockerBuild -DskipTests -P jib
  mvnw clean compile jib:dockerBuild -DskipTests -P jib
)
if [%1] == [jibtar] (
  echo mvnw clean compile jib:buildTar -DskipTests -P jib
  mvnw clean compile jib:buildTar -DskipTests -P jib
)

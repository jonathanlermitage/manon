@echo off

if [%1] == [help] (
  echo  t       test
  echo  tc      test and generate coverage data
  echo  sc      compute and upload Sonar analysis to SonarCloud
  echo  tsc     similar to "do tc" then "do sc"
  echo  fb      scan with FindBugs
  echo  fbgui   start FindBugs GUI
  echo  b       compile
  echo  c       clean
  echo  p       package
  echo  rd      package and run application with dev profile
  echo  w $V    set or upgrade Maven wrapper to version $V
  echo  cv      check plugins and dependencies versions
  echo  uv      update plugins and dependencies versions
  echo  dt      show dependencies tree
  echo  jib     build Docker image to a Docker daemon
  echo  jibtar  build and save Docker image to a tarball
)

if [%1] == [t] (
  mvnw clean test -P embed-win
)
if [%1] == [tc] (
  mvnw clean test -P embed-win,coverage
)
if [%1] == [b] (
  mvnw clean compile -DskipTests -T1
)
if [%1] == [c] (
  mvnw clean
)
if [%1] == [p] (
  mvnw clean package -DskipTests -T1
)
if [%1] == [rd] (
  mvnw clean package -DskipTests -T1
  cd target
  java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
  cd ..
)
if [%1] == [w] (
  mvn -N io.takari:maven:wrapper -Dmaven=%2
)
if [%1] == [cv] (
  mvnw versions:display-property-updates -U -P coverage,jib,embed-win,findbugs
)
if [%1] == [uv] (
  mvnw versions:update-properties -U -P coverage,jib,embed-win,findbugs
)
if [%1] == [dt] (
  mvnw dependency:tree
)
if [%1] == [sc] (
  mvnw sonar:sonar -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
)
if [%1] == [tsc] (
  mvnw clean test sonar:sonar -P embed-win,coverage -Dsonar.organization=%TK1_MANON_SONAR_ORGA% -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%TK1_MANON_SONAR_LOGIN%
)
if [%1] == [fb] (
  mvnw clean compile findbugs:findbugs -P findbugs
)
if [%1] == [fbgui] (
  mvnw findbugs:gui -P findbugs
)
if [%1] == [jib] (
  mvnw clean compile jib:dockerBuild -DskipTests -P jib
)
if [%1] == [jibtar] (
  mvnw clean compile jib:buildTar -DskipTests -P jib
)

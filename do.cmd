@echo off

if [%1] == [help] (
  echo  t: test
  echo  tc: test and generate coverage data
  echo  b: compile
  echo  c: clean
  echo  p: package
  echo  w $V: set or upgrade Maven wrapper to version $V
  echo  cv: check plugins and dependencies versions
  echo  up: update plugins and dependencies versions
  echo  dt: show dependencies tree
)

if [%1] == [t] ( 
  echo gradlew clean test
  gradlew clean test
)
if [%1] == [tc] (
  echo gradlew clean test jacocoTestReport -Pcoverage=true
  gradlew clean test jacocoTestReport -Pcoverage=true
)
if [%1] == [b] (
  echo gradlew clean compileJava compileTestJava -x test
  gradlew clean compileJava compileTestJava -x test
)
if [%1] == [c] (
  echo gradlew clean
  gradlew clean 
)
if [%1] == [p] ( 
  echo gradlew clean uberJar -x test
  gradlew clean uberJar -x test
)
if [%1] == [w] ( 
  echo gradle wrapper --gradle-version=%2
  gradle wrapper --gradle-version=%2
)
if [%1] == [cv] (
  echo gradlew dependencyUpdates -Drevision=release -DoutputFormatter=plain -DoutputDir=./build/
  gradlew dependencyUpdates -Drevision=release -DoutputFormatter=plain -DoutputDir=./build/
)
if [%1] == [uv] (
  echo TODO
)
if [%1] == [dt] (
  echo TODO
)

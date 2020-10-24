#!/bin/bash

TitleColor=$(tput setaf 4; tput bold)
DetailsColor=$(tput setaf 5)
ErrorColor=$(tput setaf 1; tput bold)
SuccessColor=$(tput setaf 2; tput bold)
ResetColor=$(tput sgr0)

DOCKER_REPORT=/tmp/manon_check_docker.txt
FINAL_REPORT=/tmp/manon_final_report.txt

if test -f "$DOCKER_REPORT"; then
    rm "$DOCKER_REPORT"
fi
if test -f "$FINAL_REPORT"; then
    rm "$FINAL_REPORT"
fi

echo "$TitleColor-------------------------------------"
echo "  Start application"
echo "-------------------------------------$ResetColor"
docker ps -a
./do up

echo "Let application start, wait 30s"
sleep 30 # actually, should loop until health-check returns OK

echo "$TitleColor-------------------------------------"
echo "  Run e2e tests"
echo "-------------------------------------$ResetColor"
./do gatling
docker ps --all

# find latest Gatling report. Python script will check this report.
GATLING_REPORT=$HOME/manon-gatling-results/$(ls -Alt ~/manon-gatling-results | head -2 | tail -1 | awk '{print $9}')/simulation.log
echo "Gatling report ($GATLING_REPORT):$DetailsColor"
cat "$GATLING_REPORT"
echo "$ResetColor"

# list running images (help: https://docs.docker.com/engine/reference/commandline/ps/). Python
# script will check that everything should be up and running, even after e2e tests
docker ps --all --no-trunc --filter "status=running" --format "{{.Names}}" > "$DOCKER_REPORT"
echo "Docker report ($DOCKER_REPORT):$DetailsColor"
cat "$DOCKER_REPORT"
echo "$ResetColor"

echo "Stop applications"
./do stop

echo "$TitleColor-------------------------------------"
echo "  Check reports"
echo "-------------------------------------$ResetColor"
python3 ./e2e/CheckReports.py "$DOCKER_REPORT" "$GATLING_REPORT" "$FINAL_REPORT"
echo ""

if grep -q SUCCESS "$FINAL_REPORT"; then
    echo "$SuccessColor""SUCCESS$ResetColor"
    exit 0
fi

echo "$ErrorColor""FAILURE$ResetColor"
exit 1

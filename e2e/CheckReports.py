import os
import sys


def validate_docker_report(_docker_report):
    error_count = 0
    expected_images = {"grafana",
                       "greenmail",
                       "manon",
                       "maria",
                       "maria-batch",
                       "nginx",
                       "prometheus",
                       "redis"}
    report_file = open(_docker_report, 'r')
    report_lines = report_file.readlines()
    for line in report_lines:
        if line.strip() in expected_images:
            expected_images.remove(line.strip())
    nb_images_missing = len(expected_images)
    if nb_images_missing != 0:
        error_count = nb_images_missing
        print(f"FAILURE: Some images ({nb_images_missing}) are not running: {expected_images}")
    return error_count


def validate_gatling_report(_gatling_report):
    error_count = 0
    report_file = open(_gatling_report, 'r')
    report_lines = report_file.readlines()
    for line in report_lines:
        cleaned_line = line.strip()
        if cleaned_line.startswith("REQUEST"):
            if not cleaned_line.endswith("OK"):
                error_count = error_count + 1
                print(f"FAILURE: Found failed Gatling test: {cleaned_line}")
    return error_count


def write_final_report(_final_report, _docker_test_result, _gatling_test_result):
    if os.path.exists(_final_report):
        os.remove(_final_report)
    report_file = open(_final_report, 'w')
    if _docker_test_result == 0 and _gatling_test_result == 0:
        report_file.writelines("SUCCESS")
        print("SUCCESS: Final report is OK  ฅ^•ﻌ•^ฅ")
    else:
        report_file.writelines("FAILURE")
        print("FAILURE: Final report is KO  ¯\\_(ツ)_/¯")
    report_file.close()


if __name__ == '__main__':
    docker_report = sys.argv[1]
    gatling_report = sys.argv[2]
    final_report = sys.argv[3]
    print(f"Will use Docker report: {docker_report}")
    print(f"Will use Gatling report: {gatling_report}")
    print(f"Will write Final report: {final_report}")

    docker_test_result = validate_docker_report(docker_report)
    gatling_test_result = validate_gatling_report(gatling_report)
    write_final_report(final_report, docker_test_result, gatling_test_result)

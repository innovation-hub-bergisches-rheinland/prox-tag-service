#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

export IMAGE="$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.artifactId -q -DforceStdout)"

docker stack rm "${IMAGE}"

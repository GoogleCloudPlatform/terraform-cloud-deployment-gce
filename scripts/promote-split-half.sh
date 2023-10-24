#!/bin/bash
# Copyright 2023 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

JSS_PROJECT_PREFIX="cloud-deployment-gce-java"
URL_MAP_NAME="${JSS_PROJECT_PREFIX}-user"
CDN_BACKEND="${JSS_PROJECT_PREFIX}-cdn"
BULE_V1_BACKEND="${JSS_PROJECT_PREFIX}-blue-v1"
GREEN_V2_BACKEND="${JSS_PROJECT_PREFIX}-green-v2"
URL_MAP_CONFIG_FILE="${URL_MAP_NAME}-url-map.yaml"
PROJECT_ID="$(gcloud config get-value project | tail -1)"

cat << EOF > ./${URL_MAP_CONFIG_FILE}
defaultService: projects/$PROJECT_ID/global/backendBuckets/$CDN_BACKEND
hostRules:
- hosts:
  - '*'
  pathMatcher: app
name: ${URL_MAP_NAME}
pathMatchers:
- defaultService: projects/$PROJECT_ID/global/backendBuckets/$CDN_BACKEND
  name: app
  pathRules:
  - paths:
    - /resource/*
    service: projects/$PROJECT_ID/global/backendBuckets/$CDN_BACKEND
  - paths:
    - '*'
    routeAction:
      weightedBackendServices:
      - backendService: projects/$PROJECT_ID/global/backendServices/$BULE_V1_BACKEND
        weight: 500
      - backendService: projects/$PROJECT_ID/global/backendServices/$GREEN_V2_BACKEND
        weight: 500
EOF

gcloud compute url-maps import ${URL_MAP_NAME} --source=${URL_MAP_CONFIG_FILE} --quiet
gcloud compute url-maps describe ${URL_MAP_NAME}
gcloud compute forwarding-rules list --filter="name=${URL_MAP_NAME}"

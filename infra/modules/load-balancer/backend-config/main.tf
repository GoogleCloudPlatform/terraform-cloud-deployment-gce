/**
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

resource "google_compute_health_check" "cloud_deployment" {
  name                = "cloud-deployment-gce-java-${var.name_suffix}"
  timeout_sec         = 10
  check_interval_sec  = 20
  unhealthy_threshold = 2
  tcp_health_check {
    port = var.health_check_port
  }
  log_config {
    enable = true
  }
}

resource "google_compute_backend_service" "cloud_deployment" {
  name                  = "cloud-deployment-gce-java-${var.name_suffix}"
  load_balancing_scheme = "EXTERNAL_MANAGED"
  port_name             = var.port_name
  dynamic "backend" {
    for_each = var.backends
    iterator = entry
    content {
      group = entry.value["group_url"]
    }
  }
  health_checks = [google_compute_health_check.cloud_deployment.self_link]
}

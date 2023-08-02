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

resource "google_compute_url_map" "cloud_deployment" {
  name            = "cloud-deployment-gce-java-${var.name_suffix}"
  default_service = var.backend_bucket_id
  host_rule {
    path_matcher = "app"
    hosts = [
      "*",
    ]
  }
  path_matcher {
    name            = "app"
    default_service = var.backend_bucket_id
    path_rule {
      paths = [
        "/${var.resource_path}/*",
      ]
      service = var.backend_bucket_id
    }
    path_rule {
      paths = [
        "*",
      ]
      route_action {
        dynamic "weighted_backend_services" {
          for_each = var.weighted_backend_services
          iterator = entry
          content {
            backend_service = entry.value["backend_service_id"]
            weight          = lookup(entry.value, "weight", floor(1000 / length(var.weighted_backend_services)))
          }
        }
      }
    }
  }
}

resource "google_compute_target_http_proxy" "cloud_deployment" {
  name    = "cloud-deployment-gce-java-${var.name_suffix}"
  url_map = google_compute_url_map.cloud_deployment.self_link
}

resource "google_compute_global_forwarding_rule" "cloud_deployment" {
  name                  = "cloud-deployment-gce-java-${var.name_suffix}"
  target                = google_compute_target_http_proxy.cloud_deployment.self_link
  ip_address            = var.global_address
  port_range            = "80"
  load_balancing_scheme = "EXTERNAL_MANAGED"
  labels                = var.labels
}

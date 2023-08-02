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

resource "google_compute_network" "main" {
  name                    = "cloud-deployment-gce-java"
  project                 = var.project_id
  auto_create_subnetworks = true
}

resource "google_compute_firewall" "cloud_deployment" {
  name      = "cloud-deployment-gce-java-health-check"
  network   = google_compute_network.main.name
  direction = "INGRESS"
  source_ranges = [
    "130.211.0.0/22", //health check ip
    "35.191.0.0/16"   //health check ip
  ]
  allow {
    protocol = "tcp"
    ports    = var.health_check_allow_ports
  }
}

# need to know what is google_compute_router and its nat.
resource "google_compute_router" "main" {
  name    = "cloud-deployment-gce-java-router"
  region  = var.region
  network = google_compute_network.main.name
}

resource "google_compute_router_nat" "main" {
  name                               = "cloud-deployment-gce-java-router-nat"
  router                             = google_compute_router.main.name
  region                             = google_compute_router.main.region
  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"
}

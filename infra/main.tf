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

module "project_services" {
  source  = "terraform-google-modules/project-factory/google//modules/project_services"
  version = "~> 14.0"

  project_id                  = var.project_id
  disable_services_on_destroy = false
  activate_apis = [
    "compute.googleapis.com",
    "iam.googleapis.com",
    "cloudresourcemanager.googleapis.com",
    "sqladmin.googleapis.com",
    "secretmanager.googleapis.com",
    "servicenetworking.googleapis.com",
  ]
}

data "google_project" "project" {
  depends_on = [
    module.project_services
  ]

  project_id = var.project_id
}

locals {
  resource_path = "resource"
  named_ports_map = {
    "client" = {
      name = "lds-client-port"
      port = 80
    },
    "server" = {
      name = "lds-server-port"
      port = 8000
    },
  }
  named_ports = [
    for key, value in local.named_ports_map : {
      name = value.name
      port = value.port
    }
  ]
}

resource "google_service_account" "cloud_deployment" {
  depends_on = [
    module.project_services,
  ]

  account_id = "cloud-deployment-gce-java"
}

resource "google_project_iam_member" "cloud_deployment" {
  for_each = toset([
    "roles/storage.objectAdmin",
    "roles/cloudsql.editor",
    "roles/logging.logWriter",
  ])

  project = data.google_project.project.project_id
  role    = each.key
  member  = "serviceAccount:${google_service_account.cloud_deployment.email}"
}

module "storage" {
  source = "./modules/storage"

  project_id = data.google_project.project.project_id
  location   = var.bucket_location
  labels     = var.labels
  name       = "cloud-deployment-gce-resource-${data.google_project.project.number}"
}

module "networking" {
  source = "./modules/networking"

  project_id = data.google_project.project.project_id
  region     = var.region
  health_check_allow_ports = [
    for key, value in local.named_ports_map : value.port
  ]
}

module "database" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/database"

  project_id            = data.google_project.project.project_id
  region                = var.region
  zone                  = var.zone
  vpc_network_self_link = module.networking.vpc_network_self_link
  availability_type     = "ZONAL"
  service_account       = google_service_account.cloud_deployment.email
}

resource "google_compute_backend_bucket" "cdn" {
  project     = data.google_project.project.project_id
  name        = "cloud-deployment-gce-java-cdn"
  bucket_name = module.storage.bucket_name
  enable_cdn  = true
  cdn_policy {
    cache_mode        = "CACHE_ALL_STATIC"
    client_ttl        = 3600
    default_ttl       = 3600
    max_ttl           = 86400
    negative_caching  = true
    serve_while_stale = 86400
  }
  custom_response_headers = [
    "X-Cache-ID: {cdn_cache_id}",
    "X-Cache-Hit: {cdn_cache_status}",
    "X-Client-Location: {client_region_subdivision}, {client_city}",
    "X-Client-IP-Address: {client_ip_address}"
  ]
}

resource "google_compute_firewall" "cloud_deployment_client" {
  name    = "cloud-deployment-gce-java-client"
  network = module.networking.vpc_network_self_link
  allow {
    protocol = "tcp"
    ports = [
      lookup(local.named_ports_map, "client")["port"],
    ]
  }
  target_tags   = ["service"]
  source_ranges = module.global_addresses.addresses
}

resource "google_compute_firewall" "cloud_deployment_ssh" {
  name    = "cloud-deployment-gce-java-ssh"
  network = module.networking.vpc_network_self_link
  allow {
    protocol = "tcp"
    ports = [
      22,
    ]
  }
  # IAP TCP forwarding IP range.
  source_ranges = [
    "35.235.240.0/20",
  ]
}

module "global_addresses" {
  depends_on = [
    module.project_services
  ]
  source  = "terraform-google-modules/address/google"
  version = "3.2.0"

  project_id   = data.google_project.project.project_id
  region       = var.region
  address_type = "EXTERNAL"
  global       = true
  names = [
    "cloud-deployment-gce-java-blue",
    "cloud-deployment-gce-java-green",
    "cloud-deployment-gce-java-test",
  ]
}

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

module "vm_container_green" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/vm-container"

  source_image          = var.source_image
  vpc_network_self_link = module.networking.vpc_network_self_link
  service_account = {
    email = google_service_account.cloud_deployment.email
    scopes = [
      "https://www.googleapis.com/auth/cloud-platform",
    ]
  }
  project_id = data.google_project.project.project_id
  region     = var.region
  mig_name   = "cloud-deployment-gce-java-mig-green"
  metadata = {
    google-logging-enabled    = "true"
    google-monitoring-enabled = "true"
  }
  startup_script = templatefile(
    "${path.module}/templates/startup-script.tftpl",
    {
      LDS_REST_PORT            = lookup(local.named_ports_map, "server")["port"]
      LDS_PROJECT              = data.google_project.project.project_id,
      LDS_BUCKET               = module.storage.bucket_name,
      LDS_RESOURCE_PATH        = "/${local.resource_path}",
      LDS_MYSQL_INSTANCE       = module.database.connection_name,
      LDS_MYSQL_DATABASE       = module.database.database_name,
      LDS_MYSQL_USERNAME       = module.database.user_name,
      MYSQL_DB_PASSWORD_SECRET = module.database.password_secret,
      FRONTEND_VERSION_TAG     = "green",
      DATA_INIT_BUCKET_NAME    = var.data_init_bucket_name,
      DATA_INIT_FILE_NAME      = var.data_init_archive_file_name,
    }
  )
  tags = [
    "service",
  ]
  named_ports       = local.named_ports
  health_check_port = lookup(local.named_ports_map, "server")["port"]
  labels            = var.labels
}

module "load_balancer_backend_config_green" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/load-balancer/backend-config"

  name_suffix = "green-v2"
  backends = [
    {
      group_url = module.vm_container_green.instance_group_url,
    },
  ]
  port_name         = lookup(local.named_ports_map, "client")["name"]
  health_check_port = lookup(local.named_ports_map, "client")["port"]
}

module "load_balancer_frontend_config_green" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/load-balancer/frontend-config"

  name_suffix       = "green-v2"
  backend_bucket_id = google_compute_backend_bucket.cdn.id
  resource_path     = local.resource_path
  global_address    = module.global_addresses.addresses[1]
  weighted_backend_services = [
    {
      backend_service_id = module.load_balancer_backend_config_green.backend_service_id
    }
  ]
  labels = var.labels
}

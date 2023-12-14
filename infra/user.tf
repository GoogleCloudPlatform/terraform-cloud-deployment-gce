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

module "load_balancer_frontend_config_user" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/load-balancer/frontend-config"

  name_suffix       = "user"
  backend_bucket_id = google_compute_backend_bucket.cdn.id
  resource_path     = local.resource_path
  global_address    = module.global_addresses.addresses[2]
  weighted_backend_services = [
    {
      backend_service_id = module.load_balancer_backend_config_blue.backend_service_id
    }
  ]
  labels = var.labels
}

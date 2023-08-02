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

output "backend_bucket_name" {
  description = "The name of the backend bucket used for Cloud CDN"
  value       = google_compute_backend_bucket.cdn.name
}

output "bucket_name" {
  description = "Bucket name"
  value       = module.storage.bucket_name
}

output "blue_mig_self_link" {
  description = "Self link of the blue version instance group"
  value       = module.vm_container_blue.mig_self_link
}

output "green_mig_self_link" {
  description = "Self link of the green version instance group"
  value       = module.vm_container_green.mig_self_link
}

output "blue_mig_load_balancer_ip" {
  description = "IP address of the blue version instance group"
  value       = "http://${module.load_balancer_frontend_config_blue.lb_external_ip}"
}

output "green_mig_load_balancer_ip" {
  description = "IP address of the green version instance group"
  value       = "http://${module.load_balancer_frontend_config_green.lb_external_ip}"
}

output "user_mig_load_balancer_ip" {
  description = "IP address of the user instance group"
  value       = "http://${module.load_balancer_frontend_config_user.lb_external_ip}"
}

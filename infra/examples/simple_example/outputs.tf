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

output "bucket_name" {
  description = "Bucket name"
  value       = module.simple.bucket_name
}

output "blue_mig_self_link" {
  description = "Self link of the blue version instance group"
  value       = module.simple.blue_mig_self_link
}

output "green_mig_self_link" {
  description = "Self link of the green version instance group"
  value       = module.simple.green_mig_self_link
}

output "blue_mig_load_balancer_ip" {
  description = "IP address of the blue version instance group"
  value       = module.simple.blue_mig_load_balancer_ip
}

output "green_mig_load_balancer_ip" {
  description = "IP address of the green version instance group"
  value       = module.simple.green_mig_load_balancer_ip
}

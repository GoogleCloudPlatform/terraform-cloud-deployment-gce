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

variable "source_image" {
  description = "The source image used to create the instance."
  type        = string
}

variable "vpc_network_self_link" {
  description = "VPC network self link to deploy the instance in"
  type        = string
}

variable "service_account" {
  description = "Google recommends custom service accounts that have cloud-platform scope and permissions granted via IAM Roles."
  type = object({
    email  = string
    scopes = set(string)
  })
}

variable "metadata" {
  type        = map(any)
  description = "metadata to attach to the instance"
  default     = {}
}

variable "startup_script" {
  type        = string
  description = "The startup script to be used on the instance."
  default     = ""
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
}

variable "project_id" {
  description = "Google Cloud project ID."
  type        = string
}

variable "region" {
  description = "The region chosen to be used."
  type        = string
}

variable "mig_name" {
  description = "managed instance group name"
  type        = string
}

variable "hostname" {
  description = "hostname for the instance"
  type        = string
  default     = ""
}

variable "named_ports" {
  description = "named ports"
  type = list(object(
    {
      name = string
      port = number
    }
  ))
}

variable "health_check_port" {
  description = "health check port"
  type        = number
}

variable "tags" {
  description = "network tags for the instance"
  type        = list(string)
}

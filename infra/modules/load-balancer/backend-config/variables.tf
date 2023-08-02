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

variable "name_suffix" {
  description = "Suffix to append to the name of each resource."
  type        = string
}

variable "backends" {
  description = "A map of key/value pairs to assign load balancer group."
  type = list(object(
    {
      group_url = string
    }
  ))
}

variable "port_name" {
  description = "port name"
  type        = string
}

variable "health_check_port" {
  description = "health check port"
  type        = number
}

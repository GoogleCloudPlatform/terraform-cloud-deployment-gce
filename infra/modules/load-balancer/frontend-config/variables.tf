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

variable "backend_bucket_id" {
  description = "backend bucket id"
  type        = string
}

variable "resource_path" {
  description = "Resource folder path"
  type        = string
}

variable "labels" {
  description = "A map of key/value label pairs to assign to the bucket."
  type        = map(string)
}

variable "weighted_backend_services" {
  description = "A list of weighted backend services."
  type = list(object(
    {
      backend_service_id = string
    }
  ))
}

variable "global_address" {
  description = "LB global address"
  type        = string
}

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

variable "project_id" {
  description = "GCP project ID."
  type        = string
  validation {
    condition     = var.project_id != ""
    error_message = "Error: project_id is required"
  }
}

variable "region" {
  description = "GCP region where the cloud resource will be created."
  type        = string
  default     = "us-central1"
}

variable "zones" {
  description = "GCP zones where the cloud resource will be created."
  type        = list(string)
  default     = ["us-central1-a"]
}

variable "bucket_location" {
  description = "Bucket location where the uploaded files will be stored."
  type        = string
  default     = "US"

  validation {
    condition     = contains(["ASIA", "EU", "US"], var.bucket_location)
    error_message = "Allowed values for type are \"ASIA\", \"EU\", \"US\"."
  }
}

variable "source_image" {
  description = "The source image used to create the instance."
  type        = string
  default     = "https://www.googleapis.com/compute/beta/projects/hsa-public/global/images/jss-cd-gce-vm-image"
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the cloud resources."
  default = {
    app = "cloud-deployment-gce-java"
  }
}

variable "data_init_bucket_name" {
  description = "Name of the Cloud Storage bucket that store the archive file for initialization."
  type        = string
  default     = "jss-resources"
}

variable "data_init_archive_file_name" {
  description = "Name of the archive file that contains the initialization data."
  type        = string
  default     = "initialization.tar.gz"
}

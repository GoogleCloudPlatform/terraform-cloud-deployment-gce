// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

packer {
  required_plugins {
    googlecompute = {
      version = "1.0.16"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}

source "googlecompute" "main" {
  project_id            = "${var.project_id}"
  image_storage_locations = [
    "${var.region}",
  ]
  zone              = "${var.zone}"
  image_name        = "${var.img_name}"
  image_description = "${var.img_desc}"
  image_labels = {
    developer = "google"
  }
  image_family        = "jss-cd"
  source_image_family = "ubuntu-2204-lts"
  ssh_username        = "root"
  network             = "default"
  machine_type        = "n2-standard-2"
}

build {
  sources = ["sources.googlecompute.main"]

  provisioner "file" {
    sources     = [
      "${var.file_docker_compose_yml}",
    ]
    destination = "/"
  }

  provisioner "shell" {
    script             = "${var.file_install_package_sh}"
  }
}

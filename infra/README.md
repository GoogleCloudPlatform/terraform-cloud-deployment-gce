# Cloud Deployment on GCE

## Description

### Tagline

Deploy a change to a live stateful service without disrupting downstream customers (Java on GCE)

### Detailed

This solution showcases a live feature change on a Java app, deployed to customers using blue-green managed instance group deployments.

The resources/services/activations/deletions that this module will create/trigger are:

- Google Compute Engine
- Cloud Load Balancing
- Service Networking
- Secret Manager
- Cloud Storage
- Cloud CDN
- Cloud SQL
- IAM

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs
| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| bucket\_location | Bucket location. https://cloud.google.com/storage/docs/locations | `string` | `"US"` | no |
| data_init_bucket_name | Name of the Cloud Storage bucket that store the archive file for initialization. | `string` | jss-resources | no |
| data_init_archive_file_name | Name of the archive file that contains the initialization data. | `string` | initialization.tar.gz | no |
| disable\_services\_on\_destroy | Whether project services will be disabled when the resources are destroyed. | `bool` | `false` | no |
| init | Initialize resource or not | `bool` | `true` | no |
| labels | A map of key/value label pairs to assign to the resources. | `map(string)` | <pre>{<br>  "app": "large-data-sharing-java"<br>}</pre> | no |
| lds\_client\_image | Docker image for frontend | `string` | `"gcr.io/hsa-resources-public/hsa-lds-java-frontend:latest"` | no |
| lds\_initialization\_archive\_file\_name | Archive file's name in lds-initialization bucket | `string` | `"initialization.tar.gz"` | no |
| lds\_initialization\_bucket\_name | Bucket for cloud run job | `string` | `"jss-resources"` | no |
| lds\_server\_image | Docker image for backend | `string` | `"gcr.io/hsa-resources-public/hsa-lds-java-backend:latest"` | no |
| project\_id | GCP project ID. | `string` | n/a | yes |
| region | Google cloud region where the resource will be created. | `string` | `"us-central1"` | no |
| source_image | Source VM image to deploy on GCE. | `string` | `"https://www.googleapis.com/compute/beta/projects/hsa-public/global/images/jss-cd-gce-vm-image"` | no |
| zones | Gogle cloud zone where resources will be created. | `string` | `us-central1-a` | no |

## Outputs

| Name | Description |
|------|-------------|
| backend\_bucket\_name | The name of the backend bucket used for Cloud CDN |
| bucket\_name | Bucket name |
| lb\_external\_ip | Frontend IP address of the load balancer |
| neos\_walkthrough\_url | Neos Tutorial URL |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->

## Requirements

These sections describe requirements for using this module.

### Software

The following dependencies must be available:

- [Terraform](https://developer.hashicorp.com/terraform/downloads) v0.13
- [Terraform Provider for GCP](https://registry.terraform.io/providers/hashicorp/google/latest/docs) plugin v4.57

### Service Account

A service account with the following roles must be used to provision
the resources of this module:

- roles/secretmanager.secretAccessor
- roles/storage.objectAdmin
- roles/compute.imageUser
- roles/cloudsql.editor
- roles/logging.logWriter

### APIs

A project with the following APIs enabled must be used to host the
resources of this module:

- compute.googleapis.com
- iam.googleapis.com
- cloudresourcemanager.googleapis.com
- sqladmin.googleapis.com
- secretmanager.googleapis.com
- servicenetworking.googleapis.com

## Contributing

Refer to the [contribution guidelines](CONTRIBUTING.md) for
information on contributing to this module.

[terraform-provider-gcp]: https://www.terraform.io/docs/providers/google/index.html
[terraform]: https://www.terraform.io/downloads.html

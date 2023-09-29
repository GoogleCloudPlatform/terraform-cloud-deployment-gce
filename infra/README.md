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
| bucket\_location | Bucket location where the uploaded files will be stored. | `string` | `"US"` | no |
| data\_init\_archive\_file\_name | Name of the archive file that contains the initialization data. | `string` | `"initialization.tar.gz"` | no |
| data\_init\_bucket\_name | Name of the Cloud Storage bucket that store the archive file for initialization. | `string` | `"jss-resources"` | no |
| labels | A map of key/value label pairs to assign to the cloud resources. | `map(string)` | <pre>{<br>  "app": "cloud-deployment-gce-java"<br>}</pre> | no |
| project\_id | Google Cloud project ID. | `string` | n/a | yes |
| region | Google Cloud region where the cloud resource will be created. | `string` | `"us-central1"` | no |
| source\_image | The source image used to create the instance. | `string` | `"https://www.googleapis.com/compute/beta/projects/hsa-public/global/images/jss-cd-gce-vm-image"` | no |
| zones | Google Cloud zones where the cloud resource will be created. | `list(string)` | <pre>[<br>  "us-central1-a"<br>]</pre> | no |

## Outputs

| Name | Description |
|------|-------------|
| backend\_bucket\_name | The name of the backend bucket used for Cloud CDN |
| blue\_mig\_load\_balancer\_ip | IP address of the blue version instance group |
| blue\_mig\_self\_link | Self link of the blue version instance group |
| bucket\_name | Bucket name |
| green\_mig\_load\_balancer\_ip | IP address of the green version instance group |
| green\_mig\_self\_link | Self link of the green version instance group |
| user\_mig\_load\_balancer\_ip | IP address of the user instance group |

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

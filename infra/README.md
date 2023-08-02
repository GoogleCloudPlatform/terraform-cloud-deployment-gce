
<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| bucket\_location | Bucket location where the uploaded files will be stored. | `string` | `"US"` | no |
| data\_init\_archive\_file\_name | Name of the archive file that contains the initialization data. | `string` | `"initialization.tar.gz"` | no |
| data\_init\_bucket\_name | Name of the Cloud Storage bucket that store the archive file for initialization. | `string` | `"lds-resources-236348946525"` | no |
| labels | A map of key/value label pairs to assign to the cloud resources. | `map(string)` | <pre>{<br>  "app": "cloud-deployment-gce-java"<br>}</pre> | no |
| project\_id | GCP project ID. | `string` | n/a | yes |
| region | GCP region where the cloud resource will be created. | `string` | `"us-central1"` | no |
| source\_image | The source image used to create the instance. | `string` | `"https://www.googleapis.com/compute/beta/projects/hsa-public/global/images/jss-cd-gce-vm-image"` | no |
| zones | GCP zones where the cloud resource will be created. | `list(string)` | <pre>[<br>  "us-central1-a"<br>]</pre> | no |

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

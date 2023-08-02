
<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| health\_check\_port | health check port | `number` | n/a | yes |
| hostname | hostname for the instance | `string` | `""` | no |
| labels | A map of key/value label pairs to assign to the resources. | `map(string)` | n/a | yes |
| metadata | metadata to attach to the instance | `map(any)` | `{}` | no |
| mig\_name | managed instance group name | `string` | n/a | yes |
| named\_ports | named ports | <pre>list(object(<br>    {<br>      name = string<br>      port = number<br>    }<br>  ))</pre> | n/a | yes |
| project\_id | GCP project ID. | `string` | n/a | yes |
| region | The region chosen to be used. | `string` | n/a | yes |
| service\_account | Google recommends custom service accounts that have cloud-platform scope and permissions granted via IAM Roles. | <pre>object({<br>    email  = string<br>    scopes = set(string)<br>  })</pre> | n/a | yes |
| source\_image | The source image used to create the instance. | `string` | n/a | yes |
| startup\_script | The startup script to be used on the instance. | `string` | `""` | no |
| tags | network tags for the instance | `list(string)` | n/a | yes |
| vpc\_network\_self\_link | VPC network self link to deploy the instance in | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| instance\_group\_url | The URL of the instance group. |
| mig\_self\_link | The self link of the instance group manager. |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->

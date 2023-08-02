
<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| backend\_bucket\_id | backend bucket id | `string` | n/a | yes |
| global\_address | LB global address | `string` | n/a | yes |
| labels | A map of key/value label pairs to assign to the bucket. | `map(string)` | n/a | yes |
| name\_suffix | Suffix to append to the name of each resource. | `string` | n/a | yes |
| resource\_path | Resource folder path | `string` | n/a | yes |
| weighted\_backend\_services | A list of weighted backend services. | <pre>list(object(<br>    {<br>      backend_service_id = string<br>    }<br>  ))</pre> | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| lb\_external\_ip | Frontend IP address of the load balancer |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->

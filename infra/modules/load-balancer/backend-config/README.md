
<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| backends | A map of key/value pairs to assign load balancer group. | <pre>list(object(<br>    {<br>      group_url = string<br>    }<br>  ))</pre> | n/a | yes |
| health\_check\_port | health check port | `number` | n/a | yes |
| name\_suffix | Suffix to append to the name of each resource. | `string` | n/a | yes |
| port\_name | port name | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| backend\_service\_id | Backend service ID |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->

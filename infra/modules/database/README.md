
<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| availability\_type | The availability type of the Cloud SQL instance, high availability (REGIONAL) or single zone (ZONAL). | `string` | n/a | yes |
| project\_id | The project ID to manage the Cloud SQL resources. | `string` | n/a | yes |
| region | The region of the Cloud SQL resource. | `string` | n/a | yes |
| service\_account | Service Account which should have read permission to access the database password. | `string` | n/a | yes |
| vpc\_network\_self\_link | network\_self\_link is the self link of the VPC network to which the Cloud SQL instance is connected. | `string` | n/a | yes |
| zones | A list of zones that DB location\_preference can be placed in. The list depends on the region chosen. | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| connection\_name | The connection name of the instance to be used in connection strings. For example, when connecting with Cloud SQL Proxy. |
| database\_name | Name of the Cloud SQL database |
| db\_ip | The IPv4 address assigned for the master instance |
| password | SQL password |
| password\_secret | Name of the secret storing the database password |
| user\_name | SQL username |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->

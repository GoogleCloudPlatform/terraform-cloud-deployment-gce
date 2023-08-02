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

output "db_ip" {
  description = "The IPv4 address assigned for the master instance"
  value       = google_sql_database_instance.main.private_ip_address
}

output "connection_name" {
  description = "The connection name of the instance to be used in connection strings. For example, when connecting with Cloud SQL Proxy."
  value       = google_sql_database_instance.main.connection_name
}

output "database_name" {
  description = "Name of the Cloud SQL database"
  value       = google_sql_database.main.name
}

output "user_name" {
  description = "SQL username"
  value       = google_sql_user.main.name
}

output "password_secret" {
  description = "Name of the secret storing the database password"
  value       = google_secret_manager_secret.sql_password.secret_id
}

output "password" {
  description = "SQL password"
  value       = random_password.sql_password.result
}

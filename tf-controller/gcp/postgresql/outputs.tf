# Outputs to be used by applications
output "username" {
  value = google_sql_user.db_user.name
}

output "password" {
  value     = random_password.db_password.result
  sensitive = true
}

output "host" {
  value = google_sql_database_instance.default.public_ip_address
}

output "port" {
  value = 5432
}

output "database" {
  value = google_sql_database.default.name
}

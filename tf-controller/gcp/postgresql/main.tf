provider "google" {
  project = var.project_id
  region  = var.region
}

resource "random_password" "db_password" {
  length  = 16
  special = true
}

resource "google_sql_database_instance" "default" {
  name             = var.name
  database_version = "POSTGRES_13"
  region           = var.region
  settings {
    tier = var.size == "small" ? "db-f1-micro" : var.size == "medium" ? "db-g1-small" : "db-n1-standard-1"
    ip_configuration {
      ipv4_enabled = true
      # For a completely private setup:
      # ipv4_enabled    = false
      # private_network = google_compute_network.private_network.id
    }
  }

  deletion_protection = false  # Set to true for production
}

resource "google_sql_database" "default" {
  name     = "mydatabase"
  instance = google_sql_database_instance.default.name
}

resource "google_sql_user" "db_user" {
  instance = google_sql_database_instance.default.name
  name     = "dbuser"
  password = random_password.db_password.result
}

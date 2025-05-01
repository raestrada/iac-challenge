provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_container_cluster" "default" {
  name               = var.name
  location           = var.region
  initial_node_count = var.size == "small" ? 1 : var.size == "medium" ? 2 : 3

  # Use the latest GKE release
  release_channel {
    channel = "REGULAR"
  }

  # Use the smallest possible node configuration
  node_config {
    machine_type = var.size == "small" ? "e2-micro" : var.size == "medium" ? "e2-small" : "e2-medium"
    disk_size_gb = 30

    # Google recommends custom service accounts that have cloud-platform scope and permissions granted through IAM Roles
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }

  # Enable network policy for better security
  network_policy {
    enabled = true
  }

  # Auto-scaling settings
  cluster_autoscaling {
    enabled = true
    resource_limits {
      resource_type = "cpu"
      minimum       = 1
      maximum       = var.size == "small" ? 2 : var.size == "medium" ? 4 : 8
    }
    resource_limits {
      resource_type = "memory"
      minimum       = 1
      maximum       = var.size == "small" ? 2 : var.size == "medium" ? 8 : 16
    }
  }
}

# Outputs that will be needed for connecting to the cluster
output "cluster_name" {
  value = google_container_cluster.default.name
}

output "endpoint" {
  value = google_container_cluster.default.endpoint
}

output "kubeconfig" {
  sensitive = true
  value = {
    host                   = "https://${google_container_cluster.default.endpoint}"
    token                  = ""
    cluster_ca_certificate = base64decode(google_container_cluster.default.master_auth[0].cluster_ca_certificate)
  }
}

output "node_count" {
  value = google_container_cluster.default.initial_node_count
}

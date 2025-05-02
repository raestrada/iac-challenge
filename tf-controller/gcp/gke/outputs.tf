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

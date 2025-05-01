# Terraform Modules for Tofu-Controller

This directory contains Terraform modules that are used by Tofu-Controller to provision infrastructure on GCP. The control plane running on a local Kubernetes cluster will use these modules to create and manage resources in GCP.

## Directory Structure

```
tf-controller/
├── gcp/
│   ├── postgresql/
│   │   ├── main.tf
│   │   └── variables.tf
│   └── gke/
│       ├── main.tf
│       └── variables.tf
```

## Module Descriptions

### PostgreSQL Module (`gcp/postgresql`)

This module provisions a PostgreSQL database on Google Cloud SQL. It's designed to be minimalistic and cost-effective, with configurable parameters for size and name.

Key features:
- Configurable instance size (small, medium, large)
- Secure password generation
- Outputs for database credentials and connection information

### GKE Module (`gcp/gke`)

This module provisions a Google Kubernetes Engine (GKE) cluster. Like the PostgreSQL module, it's designed to be minimalistic and cost-effective.

Key features:
- Configurable cluster size (small, medium, large)
- Uses the smallest possible nodes for cost savings
- Auto-scaling enabled for flexibility
- Outputs cluster connection information

## Using These Modules with Tofu-Controller

These modules are designed to be used with Tofu-Controller, which is a Terraform controller for Kubernetes. The workflow is:

1. **Git Repository Source**: The modules are stored in this Git repository and pulled by Tofu-Controller.
2. **Authentication**: GCP credentials are stored in a Kubernetes secret and mounted into the Tofu-Controller pods.
3. **Terraform Execution**: Tofu-Controller executes Terraform to provision resources on GCP.
4. **Output Management**: The outputs from Terraform are stored as Kubernetes secrets for use by applications.

## Authentication

For the Terraform runner to authenticate with GCP, we create a Kubernetes secret containing the service account credentials:

```bash
kubectl create secret generic gcp-credentials \
  --namespace=flux-system \
  --from-file=credentials.json=/path/to/credentials.json
```

Then, we mount this secret into the Terraform runner pod:

```yaml
podSpec:
  containers:
    - name: "runner"
      env:
        - name: "GOOGLE_APPLICATION_CREDENTIALS"
          value: "/credentials/credentials.json"
      volumeMounts:
        - name: "gcp-credentials"
          mountPath: "/credentials"
          readOnly: true
  volumes:
    - name: "gcp-credentials"
      secret:
        secretName: "gcp-credentials"
        items:
          - key: "credentials.json"
            path: "credentials.json"
```

The Taskfile `scripts/setup/Taskfile.gcp-credentials.yml` automates this process.

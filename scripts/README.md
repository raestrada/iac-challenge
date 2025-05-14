# Setup Scripts Documentation

This directory contains automation scripts for setting up the development and deployment environment for the IDP Control Plane Lab project.

## Prerequisites

- Ubuntu OS (tested on Ubuntu 20.04 LTS and newer)
- Sudo access
- Internet connection

## Getting Started

We use [go-task](https://taskfile.dev/) for automation. The main `Taskfile.yml` in the root directory contains all the necessary tasks to set up your environment.

### Quick Start

1. Install go-task first (if not already installed):
   ```bash
   sudo sh -c "$(curl --location https://taskfile.dev/install.sh)" -- -d -b /usr/local/bin
   ```

2. Bootstrap everything:
   ```bash
   task all
   ```

3. Log out and log back in (required for MicroK8s group permissions)

4. Complete the setup:
   ```bash
   task post-bootstrap
   ```

## Available Tasks

- `task install-task`: Installs go-task if not already installed
- `task install-microk8s`: Installs MicroK8s Kubernetes cluster
- `task setup-microk8s`: Configures MicroK8s after installation
- `task install-tools`: Installs all required CLI tools (kubectl, flux, vela)
- `task bootstrap`: Initial setup - installs MicroK8s and tools
- `task post-bootstrap`: Complete setup after logging back in
- `task install-k8s-components`: Installs all Kubernetes components (Flux, KubeVela, Tofu-Controller)

## Individual Component Tasks

- `task install-kubectl`: Installs kubectl CLI
- `task install-flux`: Installs Flux CLI
- `task install-vela`: Installs KubeVela CLI
- `task install-flux-k8s`: Installs Flux in the Kubernetes cluster
- `task install-vela-k8s`: Installs KubeVela in the Kubernetes cluster
- `task install-tofu-controller`: Installs Tofu-Controller in the Kubernetes cluster

## Troubleshooting

If you encounter any issues during the setup:

1. Check that all prerequisites are met
2. Ensure you've logged out and back in after the MicroK8s installation
3. Run tasks individually to isolate any problems
4. Check the logs in the terminal output for error messages

For MicroK8s-specific issues, consult the [MicroK8s documentation](https://microk8s.io/docs).

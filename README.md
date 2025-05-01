# Infrastructure as Code Challenge - Control Plane Architecture with KubeVela and Tofu-Controller

## Overview

This project implements a modern infrastructure and application deployment approach using Kubernetes as a control plane with KubeVela and Tofu-Controller. It follows the architecture detailed in the article [Building a Highly Flexible Control Plane with Kubevela and Tofu-Controller: A Step-by-Step Guide](https://medium.com/@rodrigo.estrada/building-a-highly-flexible-control-plane-with-kubevela-and-tofu-controller-a-step-by-step-guide-61844cf3bc40) to deploy a Spring Boot microservice with PostgreSQL database on Google Cloud Platform (GCP).

> **⚠️ IMPORTANT ARCHITECTURE NOTE**  
> This solution implements a **control plane architecture**, where:
> 1. The control plane itself (MicroK8s cluster with KubeVela and Tofu-Controller) can be deployed anywhere - in this implementation, it's set up locally on Ubuntu for demonstration purposes
> 2. The actual resources (PostgreSQL database, Spring Boot app) are deployed to GCP by the control plane
>
> In production environments, the control plane would typically have its own dedicated infrastructure and separate account from the target resources for security and governance reasons.
>
> **Local Testing Note**: The installation scripts for the control plane are optimized for Ubuntu environments.

## Why a Kubernetes Control Plane Approach?

### The Power of Post-API Solutions

The fundamental advantage of this architecture is that it operates **after the Kubernetes API**. This creates a clear contract between infrastructure and application teams through Custom Resource Definitions (CRDs). This has profound implications:

1. **Clean Separation of Concerns**: Infrastructure teams can modify implementation details without impacting development teams since the contract is at the API level.

2. **Standard Interface**: All interactions happen through Kubernetes resources, providing a consistent way to manage both infrastructure and applications.

3. **Declarative Configuration**: Teams define what they want, not how to achieve it, simplifying management and reducing human error.

4. **Unified Tool Chain**: Use standard Kubernetes tools (kubectl, Helm, etc.) to manage both applications and infrastructure.

### Advantages over Direct OpenTofu/Terraform Usage

While OpenTofu (previously Terraform) is powerful for infrastructure provisioning, using it directly has limitations:

1. **Operational Complexity**: Direct OpenTofu usage requires managing state files, handling credentials, and orchestrating execution manually.

2. **Limited Integration**: OpenTofu alone doesn't integrate well with Kubernetes' ecosystem and lifecycle management.

3. **Separate Toolchains**: Teams need to maintain separate toolsets for infrastructure (OpenTofu) and applications (Kubernetes).

4. **Lack of Abstraction**: OpenTofu exposes all complexities to users with no easy way to create simplified abstractions.

Using Tofu-Controller within Kubernetes solves these problems by:

1. **GitOps Integration**: Infrastructure changes are versioned, auditable, and automatically applied from Git.

2. **Kubernetes-Native Management**: Manage infrastructure the same way you manage applications - through Kubernetes manifests.

3. **Secret Management**: Credentials and sensitive data are managed through Kubernetes secrets.

4. **Resource Dependencies**: Define relationships between infrastructure components and applications through Kubernetes.

5. **Multi-Cloud/Environment Management**: The control plane can manage resources in different environments (GCP, AWS, on-prem) from a single place.

## Modern and Maintainable Architecture

This approach represents the cutting edge of cloud-native infrastructure management:

### Composability through CRDs

Each component (database, application, network) is defined as a custom resource, allowing teams to compose complex systems from simple building blocks using standardized interfaces.

### Recursive Abstraction

KubeVela allows components to contain other components, creating powerful abstractions that hide complexity from users. For example, our `websql` component automatically provisions a PostgreSQL database and deploys a web application with proper connection settings - all with a single manifest.

### Multi-Level API Design

The architecture supports multiple levels of abstraction:

- **Infrastructure Engineers**: Define low-level components using Terraform modules.
- **Platform Engineers**: Compose these into higher-level abstractions using KubeVela.
- **Application Developers**: Consume simple, purpose-built components without understanding the underlying complexity.

### Self-Service through Abstraction

Developers can self-serve infrastructure needs without understanding the underlying details, increasing productivity while maintaining governance and security.

## Key Technologies

- **KubeVela**: Application delivery platform built on Kubernetes
- **Tofu-Controller**: Kubernetes controller for managing OpenTofu/Terraform resources
- **Flux**: GitOps toolkit for Kubernetes
- **Spring Boot**: Framework for building Java microservices
- **PostgreSQL**: Robust, open-source relational database
- **Google Cloud Platform**: Cloud provider for infrastructure resources

## Getting Started

This project uses [go-task](https://taskfile.dev/) for automation. Follow these steps to set up your environment.

### Prerequisites

- Ubuntu OS (tested on Ubuntu 20.04 LTS and newer)
- Sudo access
- Internet connection

### Installation Steps

1. **Install go-task**

   ```bash
   sudo sh -c "$(curl --location https://taskfile.dev/install.sh)" -- -d -b /usr/local/bin
   ```

2. **List available tasks**

   ```bash
   task -l
   ```

3. **Bootstrap the environment**

   Complete setup (installs everything in one step):
   ```bash
   task bootstrap
   ```
   
   This will install and configure MicroK8s, add the required paths to your environment, and install all necessary components (Flux, KubeVela, and Tofu-Controller).

### Available Tasks

#### Main Tasks

- `task bootstrap` - Complete installation (all components)
- `task install-k8s-components` - Install all Kubernetes components
- `task status` - Check status of all components
- `task access-ui` - Access KubeVela UI

#### Component-Specific Tasks

- MicroK8s tasks: `task microk8s:install`, `task microk8s:setup`, `task microk8s:status`
- Flux tasks: `task flux:install-cli`, `task flux:install`, `task flux:status`
- KubeVela tasks: `task kubevela:install-cli`, `task kubevela:install`, `task kubevela:enable-velaux`
- Tofu-Controller tasks: `task tofu:install`, `task tofu:verify`, `task tofu:status`

### Testing the Installation

After completing the setup, verify that all components are running correctly:

```bash
task status
```

Access the KubeVela UI:

```bash
task access-ui
```

This will start a port-forward to the KubeVela UI, making it accessible at http://localhost:8080 (username: admin, password: VelaUX12345).

### Next Steps

After setting up the environment, proceed to creating the Terraform modules and defining the KubeVela components for your Spring Boot application and PostgreSQL database.

For more detailed documentation, see the documentation in the `docs` directory.

# Infrastructure as Code Challenge - Control Plane Architecture with KubeVela and Tofu-Controller

## Overview

This project implements a modern infrastructure and application deployment approach using Kubernetes as a control plane with KubeVela and Tofu-Controller. It follows the architecture detailed in the article [Building a Highly Flexible Control Plane with Kubevela and Tofu-Controller: A Step-by-Step Guide](https://medium.com/@rodrigo.estrada/building-a-highly-flexible-control-plane-with-kubevela-and-tofu-controller-a-step-by-step-guide-61844cf3bc40) to deploy a Spring Boot microservice with PostgreSQL database.

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

See the documentation in the `docs` directory for detailed installation and usage instructions.# iac-challenge

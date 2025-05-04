# Infrastructure as Code Challenge - Control Plane Architecture with KubeVela and Tofu-Controller

[![App Security Scanning](https://github.com/raestrada/iac-challenge/actions/workflows/app-security-scan.yml/badge.svg)](https://github.com/raestrada/iac-challenge/actions/workflows/app-security-scan.yml)
[![Build and Push App](https://github.com/raestrada/iac-challenge/actions/workflows/build-push-app.yml/badge.svg)](https://github.com/raestrada/iac-challenge/actions/workflows/build-push-app.yml)
[![Kubernetes Manifests CI](https://github.com/raestrada/iac-challenge/actions/workflows/k8s-manifests-ci.yml/badge.svg)](https://github.com/raestrada/iac-challenge/actions/workflows/k8s-manifests-ci.yml)
[![Terraform CI](https://github.com/raestrada/iac-challenge/actions/workflows/terraform-ci.yml/badge.svg)](https://github.com/raestrada/iac-challenge/actions/workflows/terraform-ci.yml)

## Modern Production-Grade GitOps Control Plane

This project implements a production-ready control plane for managing infrastructure and applications on Google Cloud Platform (GCP) using KubeVela, Tofu-Controller, and Flux CD. The solution follows GitOps principles, emphasizing declarative configurations, version control, and automated reconciliation.

> 📑 **Security & Cost Optimization**: View our detailed analysis on [cloud security and cost optimization strategies](docs/security-cost-optimization.md) for production environments.
> 
> 🔧 **CI/CD Workflows**: Explore our [automated CI/CD workflows](docs/ci-cd-workflows.md) for application, infrastructure, and manifest validation.
> 
> 🔒 **Exposure Options**: Learn how to expose services via [Nginx Ingress or Cloudflare Tunnel](docs/exposure-trait.md) using our configurable trait.
> 
> 🚀 **GKE Ingress**: Deploy [Nginx Ingress Controller on GKE](docs/nginx-ingress-gke.md) with our optimized KubeVela manifests and Taskfiles.
> 
> 🤖 **AI Assistance**: Read about how [AI was used in this project](docs/ai-assistance.md) while maintaining human-directed architecture and implementation.

## Key Features

- **GitOps-Powered Infrastructure & Applications**: Everything is defined as code in Git and automatically reconciled
- **Highly Modular Architecture**: Well-separated components with clear interfaces between them
- **Comprehensive CI/CD**: Automated testing, security scanning, and deployment pipelines
- **Security-First Approach**: OWASP dependency checks, Trivy scanning, TruffleHog secret detection, and more
- **Managed Services Integration**: Grafana Cloud for monitoring, SonarQube Cloud for code quality, Infracost for cost optimization
- **Google Cloud Integration**: GKE, Cloud SQL, Artifact Registry, and IAM properly configured

## CI/CD Workflows

This project uses GitHub Actions for robust CI/CD pipeline implementation:

### 1. Application Security Scanning
- **Workflow**: [app-security-scan.yml](/.github/workflows/app-security-scan.yml)
- **Purpose**: Comprehensive security scanning of Spring Boot application
- **Tools**: 
  - OWASP Dependency Check for vulnerable dependencies
  - SpotBugs for static code analysis
  - Trivy for vulnerability scanning
  - SonarQube for code quality (optional via GitHub variables)
- **Triggers**: On push to main, pull requests, weekly schedule, or manual trigger

### 2. Application Build & Deploy
- **Workflow**: [build-push-app.yml](/.github/workflows/build-push-app.yml)
- **Purpose**: Build and publish Docker image to Google Artifact Registry
- **Implementation**: 
  - Uses multi-stage Dockerfile for efficient builds
  - Authenticates with Google Cloud
  - Creates Artifact Registry repository if needed
  - Tags images with commit SHA and timestamp
- **Triggers**: After successful security scanning or manual trigger

### 3. Kubernetes Manifests Validation
- **Workflow**: [k8s-manifests-ci.yml](/.github/workflows/k8s-manifests-ci.yml)
- **Purpose**: Validate Kubernetes/KubeVela manifests
- **Tools**:
  - yamllint for YAML syntax validation
  - Kubeconform for schema validation
  - Kube-linter for best practices
- **Triggers**: Changes to manifest files, pull requests, or manual trigger

### 4. Terraform/OpenTofu Validation
- **Workflow**: [terraform-ci.yml](/.github/workflows/terraform-ci.yml)
- **Purpose**: Validate infrastructure code and estimate costs
- **Tools**:
  - Terraform validation
  - TFLint for linting
  - Infracost for cost estimation
- **Triggers**: Changes to tf-controller directory, pull requests, or manual trigger

![architecture](https://miro.medium.com/v2/resize:fit:1400/0*UF_t_MBXZ-wq0Z3t)

## From Complex Infrastructure to a Single YAML

This project demonstrates how infrastructure complexity can be abstracted into a simple, declarative YAML file. Instead of managing numerous resources across multiple tools, developers can deploy an entire application stack with a single command:

```yaml
apiVersion: core.oam.dev/v1beta1
kind: Application
metadata:
  name: my-spring-app
  namespace: default
spec:
  components:
    - name: web-app
      type: spring-app-with-db
      properties:
        name: my-online-store
        image: "example/spring-app:latest"
        version: "1.0.0"
        projectId: "${GCP_PROJECT_ID}"
        region: "${GCP_REGION}"
        dbConfig:
          name: "online-store-db"
          size: "small"
```

With just this YAML file, the system will:

1. **Provision Infrastructure** - Creates a PostgreSQL database in Google Cloud
2. **Deploy Application** - Deploys your Spring Boot application
3. **Connect Components** - Automatically configures database connections
4. **Apply Best Practices** - Follows infrastructure best practices and security patterns

All of this happens automatically through the GitOps-powered control plane, eliminating manual steps and configuration drift.

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

## Managed Services Integration

This project leverages several managed services to enhance monitoring, cost management, and code quality:

### 1. Grafana Cloud

Integrated for comprehensive monitoring of both infrastructure and applications:

- **Metrics Monitoring**: Collects and visualizes performance metrics from the Kubernetes cluster and applications
- **Logs Management**: Centralizes log collection and analysis from all components
- **Distributed Tracing**: Tracks request flows across microservices

### 2. SonarQube Cloud

Implemented for continuous code quality and security analysis:

- **Static Code Analysis**: Identifies code quality issues and bugs
- **Security Vulnerability Detection**: Discovers potential security vulnerabilities
- **Code Coverage Tracking**: Monitors test coverage over time

### 3. Infracost

Utilized for infrastructure cost optimization:

- **Cost Estimation**: Provides cost estimates for infrastructure changes during CI/CD
- **Cost Comparison**: Compares costs between different infrastructure configurations
- **Budget Alerts**: Helps prevent unexpected cost increases

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

## Components Architecture

This control plane solution follows a layered hierarchical approach, where infrastructure is separated from applications, but applications can easily consume infrastructure through well-defined interfaces.

### Component Hierarchy

The component hierarchy follows a three-level structure:

1. **Infrastructure Layer**: Shared resources (GKE cluster)
2. **Application Infrastructure Layer**: App-specific infrastructure (PostgreSQL)
3. **Application Layer**: The actual application (Spring Boot services)

The control plane uses KubeVela's component model to represent these layers:

```
Infrastructure Layer (Shared)
└── GKE Cluster
    ├── Application 1
    │   ├── PostgreSQL Database
    │   └── Spring Boot Service
    │
    └── Application 2
        ├── PostgreSQL Database
        └── Spring Boot Service
```

### Available Components

The following components are available in this implementation:

#### Infrastructure Components
- **terraform-gke**: Provisions a minimal GKE cluster in GCP
  - Implemented as a KubeVela ComponentDefinition that uses Tofu-Controller
  - Located in `manifests/components/gke-component.yaml`

#### Application Infrastructure Components
- **terraform-postgresql**: Provisions a dedicated PostgreSQL instance in GCP Cloud SQL
  - Implemented as a KubeVela ComponentDefinition that uses Tofu-Controller
  - Located in `manifests/components/postgresql-component.yaml`
  - Outputs connection information as Kubernetes secrets

#### Application Components
- **spring-boot-app**: Deploys a Spring Boot application in Kubernetes
  - Implements common configuration patterns for Spring applications
  - Located in `manifests/components/spring-boot-component.yaml`

#### High-Level Components
- **spring-app-with-db**: A composite component that bundles:
  - PostgreSQL database configuration and provisioning
  - Spring Boot application with auto-configured database connection
  - Service binding to connect the application to the database
  - Sequential workflow for proper deployment ordering
  - Located in `manifests/components/spring-app-with-db-component.yaml`

#### Supporting Traits
- **service-binding**: Connects applications to infrastructure by injecting credentials
  - Located in `manifests/traits/service-binding-trait.yaml`
  - Used to bind PostgreSQL connection information to Spring Boot apps

### How Components Work Together

#### Separation of Concerns

1. **Infrastructure teams** manage the shared GKE cluster using the `terraform-gke` component
2. **Platform teams** create high-level components like `spring-app-with-db`
3. **Application teams** use high-level components, providing only minimal configuration

#### Resource Provisioning Flow

1. The shared GKE cluster is provisioned first using the `gke-application.yaml`
2. Application teams deploy their applications using `spring-app-with-db` component:
   - The component automatically provisions a PostgreSQL instance
   - It configures a Spring Boot application with the correct database connection
   - It creates a service binding to inject credentials

#### Information Flow

1. Infrastructure components generate outputs (e.g., connection strings, credentials)
2. These outputs are stored as Kubernetes secrets
3. Application components consume these secrets through service binding
4. KubeVela manages dependencies to ensure resources are created in the correct order

### Using the Components

#### Deploying Shared Infrastructure

To deploy the shared GKE cluster:

```bash
task components:deploy-gke
```

#### Deploying an Application

To deploy a Spring Boot application with its own PostgreSQL database:

```bash
task components:deploy-petclinic
```

#### Deploying the Complete Stack

To deploy both the shared infrastructure and application:

```bash
task components:deploy-all
```

#### Checking Status and Connections

```bash
# View status of all components
task components:check-status

# Get database connection information
task components:get-connection-info
```

#### Cleanup

```bash
# Remove just application resources
task components:clean-app-only

# Remove all resources including shared infrastructure
task components:clean-up
```

For more detailed examples, see the sample applications in the `manifests/applications/` directory.

## GitOps Deployment Flow

This project implements a GitOps deployment workflow using Flux to continuously monitor and apply changes from your Git repository. This approach eliminates the need for manual `kubectl apply` or `vela up` commands, ensuring that your infrastructure and applications are always in sync with your Git repository.

### GitOps Architecture

```
┌───────────────┐     ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│  Git Commit   │────►│  Flux Detects │────►│ KubeVela CRDs │────►│  Resources    │
│  & Push       │     │  Changes      │     │  Applied      │     │  Provisioned  │
└───────────────┘     └───────────────┘     └───────────────┘     └───────────────┘
```

The GitOps workflow is as follows:

1. **Define Resources in Git**:
   - Infrastructure components (GKE, PostgreSQL) are defined as KubeVela YAML manifests
   - Application components (Spring Boot services) are defined in the same repository
   - All configurations are stored in the `deployments/{environment}` directories

2. **Flux Monitors Repository**:
   - Flux continuously watches the Git repository for changes
   - When changes are detected, Flux pulls the latest version and applies the changes
   - Deployment directories: `deployments/staging` and `deployments/production`

3. **Automated Reconciliation**:
   - Flux applies KubeVela manifests (Application, ComponentDefinition, etc.)
   - KubeVela controllers process these manifests
   - Tofu-Controller provisions the actual infrastructure in GCP
   - Application deployments are created in Kubernetes

4. **No Manual Commands Needed**:
   - No need to run `vela up` or `kubectl apply` commands
   - The system automatically detects drift and reconciles state
   - The Git repository becomes the single source of truth

### Setting Up GitOps Deployments

To configure Flux to monitor your deployment directories:

```bash
# Configure Flux to monitor staging and production directories
task setup-flux-deployments
```

To check the status of your Flux Kustomizations:

```bash
# View status of Flux GitOps deployments
task flux-deployments:check-status
```

### Usage Workflow

1. **Development Workflow**:
   - Make changes to KubeVela manifests in the `deployments/staging` directory
   - Commit and push changes to the Git repository
   - Flux automatically detects and applies changes within 1 minute
   - Monitor deployment status with `task flux-deployments:check-status`

2. **Production Deployment**:
   - Once tested in staging, copy or merge manifests to `deployments/production`
   - Commit and push changes to the Git repository
   - Flux automatically applies changes to the production environment
   - Production deployment happens without manual intervention

3. **Rollback Process**:
   - To rollback, revert the commit in the Git repository
   - Flux automatically detects the change and reverts the deployment
   - The system returns to the previous known-good state

### Benefits of This Approach

- **Consistency**: All environments are deployed using the same method
- **Auditability**: Git history provides a complete audit trail of all changes
- **Automation**: Reduced human error through automated deployment process
- **Self-documenting**: The Git repository serves as documentation of your infrastructure
- **Simplified Operations**: Operators focus on Git operations instead of complex deployment commands

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

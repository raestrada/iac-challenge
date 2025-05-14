# CI/CD Workflows Documentation

This document outlines the CI/CD workflows implemented for the IDP Control Plane Lab project. These workflows enable automated testing, security scanning, and deployment of all components in the solution.

## Overview

![CI/CD Flow](https://mermaid.ink/img/pako:eNqNksFu2zAMhl_F4K5dgLrYVrfJ5mLoDmvRYeihww6CQNtMIkwWPVlOsxZ59wmy7a07bD5Yov6P_Emi23p0HtOKXv0-hOz7MQxIl6-IKxeqLLPPOPrBNUwP2WJxPabEkCrzgVbqBcCDZM_gukU36dBIVZVnL0VkjNHzVhMf_xZKbUoQjXpTmhoRi5eI1WLNpKALFSu84-TDgHnYDpQ7ZE_AEYPPwKTZ1nylVe8QH_9cBK7ZwYgXk2GMLmcxR_dVZZXhyrzEDluO1KXg2_7EvgHPRd6cxD00yNkGv_0oRUEZaYDHbmhNzpK4d5jHsUMfnHvnWl_cOaVB661vH2r7o6lX7WpdV9vFcmEarKLmZl3XN1gcM0_YO4kGLCGxlE41KrN91rtDa7sjfuP37dbgMfZj8JFedTu3S0e73NNgJgZpV7YD7-0uGBpOV8qWvPYDVvhw-AwmB_xETXpXlmLtW90PY-bVMR1sX97vxDn8qRnH_xZQqNukEylK9DPVH-AxnRoPlQRr7vFhKDfP5P87OePhdjn8BGOk4JE)

Our CI/CD pipeline integrates multiple workflows that work together to ensure code quality, security, and reliable deployment:

1. **Security First**: All code changes must pass security scans before they can be built or deployed
2. **Infrastructure Validation**: Terraform code is validated and cost-analyzed before deployment
3. **Kubernetes Manifest Verification**: All Kubernetes and KubeVela manifests are validated against schemas
4. **Integrated with GitOps**: Changes that pass validation are automatically detected by Flux for deployment

## Application CI/CD Workflow

### 1. Security Scanning (`app-security-scan.yml`)

The first stage in the pipeline is security scanning:

```yaml
on:
  push:
    branches: [ main ]
    paths:
      - 'app-example/**'
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * 0'  # Weekly run
```

**Security checks include:**
- SAST (Static Application Security Testing) with SpotBugs
- OWASP Dependency Check for vulnerable dependencies
- TruffleHog for secret detection
- Trivy for application and container scanning

**Key security gates:**
- High severity vulnerabilities (CVSS 8.0+) will fail the build
- The build workflow will only run if this workflow passes

### 2. Build and Push (`build-push-app.yml`)

This workflow builds the application Docker image and pushes it to GCR:

```yaml
on:
  workflow_run:
    workflows: ["App Security Scanning"]
    types:
      - completed
```

**Features:**
- Only runs after security scans pass
- Builds the application using Maven
- Creates multi-tagged Docker images (latest, commit hash, timestamp)
- Pushes to Google Container Registry

## Infrastructure CI/CD Workflow

### 1. Terraform CI (`terraform-ci.yml`)

Validates all Terraform/OpenTofu code:

```yaml
on:
  push:
    branches: [ main ]
    paths:
      - 'tf-controller/**'
```

**Checks include:**
- `terraform fmt` for code style
- `terraform validate` for functionality
- TFLint for best practices
- Infracost for cost estimation
- Checkov for security and compliance

### 2. Kubernetes Manifests CI (`k8s-manifests-ci.yml`)

Validates Kubernetes and KubeVela manifests:

```yaml
on:
  push:
    branches: [ main ]
    paths:
      - 'manifests/**'
```

**Features:**
- YAML syntax validation
- Schema validation against Kubernetes and KubeVela CRDs
- Best practices analysis with kube-linter
- Flux compatibility checking for GitOps

## Integration with GitOps Flow

After code passes all CI checks and is merged:

1. Flux automatically detects changes in the repository
2. KubeVela controllers process the custom resources
3. Infrastructure and applications are deployed according to the manifests

## Security Considerations

- All workflows are path-filtered to minimize unnecessary runs
- Secrets are stored in GitHub Secrets
- Security scanning happens before any build or deployment
- Regular scheduled scans capture new vulnerabilities

## Cost Control

- Infrastructure changes receive cost estimates before deployment
- All deployments follow the resource controls defined in KubeVela templates
- Scheduled security scans minimize unnecessary workflow runs

## How to Use

### Triggering Workflows Manually

All workflows can be triggered manually using the GitHub Actions interface.

### Reading Results

Each workflow produces detailed logs and summaries:

- Security scans upload detailed reports as artifacts
- Kubernetes validations show counts of valid/invalid resources
- Terraform validation includes cost estimates on PRs

### Adding New Components

When adding new components:

1. Place application code in its directory
2. Update relevant Kubernetes manifests
3. Commit changes to trigger the appropriate workflows
4. Monitor workflow results in GitHub Actions tab

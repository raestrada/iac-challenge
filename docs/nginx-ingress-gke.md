# Nginx Ingress Controller for GKE

This document explains how to deploy and configure the Nginx Ingress Controller on Google Kubernetes Engine (GKE) using our KubeVela Application and Taskfiles.

## Components Created

1. **KubeVela Application** - `manifests/applications/nginx-ingress-app.yaml`
   - Deploys Nginx Ingress Controller using the official Helm chart
   - Optimized for GKE with Google Cloud Load Balancer integration
   - Configured with auto-scaling, resource limits, and high availability settings

2. **Taskfile** - `scripts/setup/Taskfile.nginx-ingress.yml`
   - Provides commands for installation, status checking, and management
   - Integrated with the main project Taskfile

## Features

- **GKE-Optimized Configuration**
  - Uses Google Cloud Network Load Balancer
  - Configured with Network Endpoint Groups (NEG) for improved performance
  - External traffic policy set to "Local" for better client IP preservation

- **Production-Ready Setup**
  - Horizontal Pod Autoscaling (2-5 replicas based on load)
  - Pod anti-affinity for high availability across nodes
  - Resource requests and limits to prevent resource starvation

- **Integrated SSL**
  - Optional cert-manager installation with Let's Encrypt integration
  - Automated certificate management

## Deployment Instructions

### 1. Deploy Nginx Ingress Controller

```bash
# Deploy Nginx Ingress Controller with a single command
task setup-nginx-ingress
```

This task:
- Applies the KubeVela Application for Nginx Ingress
- Waits for the deployment to complete
- Retrieves the external IP address for DNS configuration

### 2. Verify the Deployment

```bash
# Check the status of the deployment
task nginx-ingress:check-status
```

### 3. Configure DNS

Once you have the external IP address, configure your DNS provider to point your domains to this IP.

For example:
```
*.example.com.   A   <INGRESS_IP>
```

### 4. Configure SSL with Let's Encrypt (Optional)

```bash
# Install and configure cert-manager with Let's Encrypt
task nginx-ingress:configure-ssl
```

This will:
- Install cert-manager
- Configure a ClusterIssuer for Let's Encrypt production
- Provide instructions for using it with your Ingress resources

### 5. Test the Ingress Controller

```bash
# Deploy a test application with Ingress
task nginx-ingress:test-ingress
```

This deploys a sample application and Ingress resource for testing.

## Using with the Exposure Trait

The Nginx Ingress Controller works seamlessly with our [Exposure Trait](exposure-trait.md). When configuring the trait, set the exposure type to "nginx":

```yaml
traits:
  - type: exposure
    properties:
      type: "nginx"
      domain: "api.example.com"
      nginx:
        tls: true
        annotations:
          nginx.ingress.kubernetes.io/ssl-redirect: "true"
```

## Management Tasks

### Upgrade to the Latest Version

```bash
# Upgrade Nginx Ingress to the latest version
task nginx-ingress:upgrade
```

### Uninstall

```bash
# Remove Nginx Ingress Controller
task nginx-ingress:uninstall
```

## Troubleshooting

### Check Nginx Ingress Logs

```bash
# View logs from the controller
kubectl logs -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx -l app.kubernetes.io/component=controller
```

### Check Ingress Resources

```bash
# List all Ingress resources
kubectl get ingress --all-namespaces
```

### Verify Load Balancer Configuration

```bash
# Check Load Balancer details
kubectl describe service -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx
```

## Best Practices

1. **Security**: Always enable SSL for production workloads
2. **Resource Management**: Monitor resource usage and adjust autoscaling settings as needed
3. **Updates**: Regularly update to the latest version for security patches
4. **Monitoring**: Set up monitoring for Nginx Ingress metrics using Prometheus

## Additional Resources

- [Official Nginx Ingress Controller Documentation](https://kubernetes.github.io/ingress-nginx/)
- [GKE-specific Ingress Guide](https://cloud.google.com/kubernetes-engine/docs/tutorials/http-balancer)
- [cert-manager Documentation](https://cert-manager.io/docs/)

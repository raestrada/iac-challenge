# Exposure Trait: Nginx Ingress vs Cloudflare Tunnel

This document explains how to use the configurable Exposure Trait, which allows you to expose your applications through either Nginx Ingress or Cloudflare Tunnel without changing your application code.

## Overview

![Exposure Options](https://mermaid.ink/img/pako:eNp1kc1OwzAQhF_F8iVIkBKSFNpDjvyIAwgJqQf3sE22jVXHjmynalX13bGbgkRV7cWr2W9nZ9e9UdICbdgAaJWgw1Iqz-iJl1YeGF15zsCesQZFnTXa2Yn2ygTNbHJu6XoJXMI6OcAKpBcsmCmgcRGYAZm7zVx5mKh9Lxn6R3C5pzOjtaX0HTEz0vr5-oA92bI6iCDMHLTZc2I1JZeKWBZxC7lTnuDrOhNyKlR0kXBp0UMHRVTCnXnouIxFN5VHSFzw2MUc8_5qBxkqjgHcGVfYQE3Y7KEroBkBtnr1S09NxOQKH40KV7eXP0-_PY0hRm1kOiShm_iPB8U8eiCNFbzOQoaxecYGPtKx1RXkLnvN62ttnv8bfLRWHMDiZy8bLY1PNy-_OuZGvaCFB-uUyciSG4zBCTUPu6bxKoQp2NtbWxtI0uR2u83O59FohEajjGLqvg3TlGqr-1ATU_Vg_oOqIxGAahfQ2mnbMU5oeMFyYxyTpGzCZxWx-AV7-c63)

The Exposure Trait provides a unified interface for exposing services while allowing you to choose your preferred networking approach:

1. **Nginx Ingress**: Traditional Kubernetes ingress for simple HTTP/HTTPS routing
2. **Cloudflare Tunnel**: Zero-trust secure tunneling without exposing your cluster publicly

This trait follows the KubeVela pattern of abstracting infrastructure details, allowing application teams to focus on their applications while platform teams control the exposure method.

## Usage

### Prerequisites

#### For Nginx Ingress
- Nginx Ingress Controller installed in your cluster
- DNS records pointing to your Ingress Controller's load balancer

#### For Cloudflare Tunnel
- Cloudflare account with the domain configured
- Cloudflare API token with appropriate permissions
- Kubernetes secret containing Cloudflare credentials

### Configuring the Exposure Trait

Add the `exposure` trait to your application with the preferred exposure type:

```yaml
apiVersion: core.oam.dev/v1beta1
kind: Application
metadata:
  name: my-application
  namespace: default
spec:
  components:
    - name: my-service
      type: webservice
      properties:
        image: my-spring-app:latest
        ports:
          - port: 8080
      traits:
        - type: exposure
          properties:
            # Choose exposure type - "nginx" or "cloudflare"
            type: "nginx"
            domain: "my-app.example.com"
            path: "/"
            port: 8080
            # Nginx-specific configuration
            nginx:
              tls: true
              ingressClassName: "nginx"
              annotations:
                cert-manager.io/cluster-issuer: "letsencrypt-prod"
```

### Using Cloudflare Tunnel

To use Cloudflare Tunnel instead, change the configuration to:

```yaml
traits:
  - type: exposure
    properties:
      type: "cloudflare"
      domain: "secure-app.example.com"
      port: 8080
      cloudflare:
        accountId: "your-cloudflare-account-id"
        tunnelName: "my-app-tunnel"
        secretName: "cloudflare-credentials"
        zoneId: "your-cloudflare-zone-id"
```

### Creating Cloudflare Credentials Secret

Before using the Cloudflare Tunnel, create a secret with your Cloudflare credentials:

```bash
# Create credentials.json file
cat > credentials.json << EOF
{
  "AccountTag": "your-account-id",
  "TunnelID": "your-tunnel-id",
  "TunnelName": "your-tunnel-name",
  "TunnelSecret": "your-tunnel-secret"
}
EOF

# Create Kubernetes secret
kubectl create secret generic cloudflare-credentials --from-file=credentials.json
```

## Security Considerations

### Nginx Ingress

- Exposes your Kubernetes cluster through a public load balancer
- Relies on proper TLS configuration for security
- Requires network-level protection against DDoS and other attacks

### Cloudflare Tunnel

- Zero trust approach - no inbound ports need to be opened
- Authenticates and encrypts all traffic
- Provides additional Cloudflare security features:
  - DDoS protection
  - Web Application Firewall (WAF)
  - Bot management
  - Rate limiting

## Examples

### Spring Boot Application with Nginx Ingress

```yaml
apiVersion: core.oam.dev/v1beta1
kind: Application
metadata:
  name: spring-person-api
  namespace: default
spec:
  components:
    - name: person-api
      type: webservice
      properties:
        image: gcr.io/my-project/spring-person-api:latest
        ports:
          - port: 8080
        env:
          - name: SPRING_PROFILES_ACTIVE
            value: "production"
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

### Spring Boot Application with Cloudflare Tunnel

```yaml
apiVersion: core.oam.dev/v1beta1
kind: Application
metadata:
  name: spring-person-api
  namespace: default
spec:
  components:
    - name: person-api
      type: webservice
      properties:
        image: gcr.io/my-project/spring-person-api:latest
        ports:
          - port: 8080
        env:
          - name: SPRING_PROFILES_ACTIVE
            value: "production"
      traits:
        - type: exposure
          properties:
            type: "cloudflare"
            domain: "secure-api.example.com"
            cloudflare:
              accountId: "${CLOUDFLARE_ACCOUNT_ID}"
              tunnelName: "api-tunnel"
              secretName: "cloudflare-api-credentials"
              zoneId: "${CLOUDFLARE_ZONE_ID}"
```

## How It Works

### Nginx Ingress

The trait creates a Kubernetes Ingress resource that routes traffic to your service based on the hostname and path.

### Cloudflare Tunnel

The trait:
1. Adds a cloudflared sidecar container to your pod
2. Creates a ConfigMap with tunnel configuration
3. Mounts your Cloudflare credentials from a secret
4. Establishes an outbound secure tunnel to Cloudflare

## Troubleshooting

### Nginx Ingress Issues

- Check that your DNS records point to the Ingress Controller's load balancer
- Verify TLS certificates are properly configured
- Examine Ingress Controller logs for routing errors

### Cloudflare Tunnel Issues

- Verify the credentials.json file has the correct format and values
- Check the cloudflared container logs using:
  ```bash
  kubectl logs -f deployment/your-app-name -c cloudflared
  ```
- Confirm the tunnel is registered in your Cloudflare dashboard

## Best Practices

1. **Production Environments**: Use Cloudflare Tunnel for enhanced security
2. **Development Environments**: Nginx Ingress is simpler for testing
3. **Secrets Management**: Store Cloudflare credentials using external secrets
4. **Resource Limits**: Adjust resource limits for the cloudflared container based on traffic patterns

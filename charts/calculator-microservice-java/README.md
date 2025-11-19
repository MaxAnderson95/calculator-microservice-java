# Calculator Microservice Helm Chart

A Helm chart for deploying a distributed calculator microservice built with Java 21, Spring Boot 3.4.0, and React.

## Overview

This chart deploys a complete microservice architecture including:
- **Frontend Service**: API gateway with caching and request logging
- **Math Services**: Add, Subtract, Multiply, and Divide operations
- **Optional Redis**: For distributed caching
- **Optional PostgreSQL**: For persistent request logging

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+

## Installation

### Quick Start (In-Memory Mode)

Deploy with default settings (in-memory cache and database):

```bash
helm install calculator ./charts/calculator-microservice-java
```

### With PostgreSQL and Redis

Deploy with persistent storage and distributed caching:

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set database.type=postgres \
  --set cache.type=redis
```

### With Ingress

Enable ingress for external access:

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set ingress.enabled=true \
  --set ingress.className=nginx \
  --set ingress.hosts[0].host=calculator.example.com \
  --set ingress.hosts[0].paths[0].path=/ \
  --set ingress.hosts[0].paths[0].pathType=Prefix
```

## Configuration

### Global Settings

| Parameter | Description | Default |
|-----------|-------------|---------|
| `global.imagePullPolicy` | Image pull policy for all services | `IfNotPresent` |
| `global.chaos.enabled` | Enable chaos engineering globally | `false` |
| `global.chaos.percent` | Global chaos failure percentage | `""` (uses service default of 5%) |

### Database Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `database.type` | Database type: `memory` or `postgres` | `memory` |
| `database.postgres.deploy` | Deploy PostgreSQL as part of this chart | `true` |
| `database.postgres.image.repository` | PostgreSQL image repository | `postgres` |
| `database.postgres.image.tag` | PostgreSQL image tag | `16-alpine` |
| `database.postgres.database` | Database name | `postgres` |
| `database.postgres.username` | Database username | `postgres` |
| `database.postgres.password` | Database password | `postgres` |
| `database.postgres.persistence.enabled` | Enable persistent storage | `true` |
| `database.postgres.persistence.size` | PVC size | `1Gi` |
| `database.postgres.persistence.storageClass` | Storage class name | `""` |

### Cache Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `cache.type` | Cache type: `memory` or `redis` | `memory` |
| `cache.redis.deploy` | Deploy Redis as part of this chart | `true` |
| `cache.redis.image.repository` | Redis image repository | `redis` |
| `cache.redis.image.tag` | Redis image tag | `alpine` |

### Service Configuration

Each service (frontend, add, subtract, multiply, divide) has the following configurable parameters:

| Parameter | Description | Default |
|-----------|-------------|---------|
| `services.<name>.enabled` | Enable this service | `true` |
| `services.<name>.replicaCount` | Number of replicas | `1` |
| `services.<name>.image.repository` | Image repository | `calculator/<name>` |
| `services.<name>.image.tag` | Image tag | `latest` |
| `services.<name>.service.type` | Service type | `ClusterIP` |
| `services.<name>.service.port` | Service port | `80` |
| `services.<name>.chaos.enabled` | Override global chaos setting | `null` |
| `services.<name>.chaos.percent` | Override global chaos percentage | `null` |
| `services.<name>.resources` | Resource requests/limits | `{}` |

### Ingress Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `ingress.enabled` | Enable ingress | `false` |
| `ingress.className` | Ingress class name | `""` |
| `ingress.annotations` | Ingress annotations | `{}` |
| `ingress.hosts` | Ingress hosts configuration | See values.yaml |
| `ingress.tls` | TLS configuration | `[]` |

## Examples

### Example 1: Enable Chaos Engineering Globally

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set global.chaos.enabled=true \
  --set global.chaos.percent=10
```

### Example 2: Enable Chaos for Specific Services

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set global.chaos.enabled=false \
  --set services.add.chaos.enabled=true \
  --set services.add.chaos.percent=20 \
  --set services.multiply.chaos.enabled=true \
  --set services.multiply.chaos.percent=15
```

### Example 3: Production Deployment

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set database.type=postgres \
  --set database.postgres.password=SecurePassword123 \
  --set database.postgres.persistence.size=10Gi \
  --set cache.type=redis \
  --set ingress.enabled=true \
  --set ingress.className=nginx \
  --set ingress.hosts[0].host=calculator.prod.example.com \
  --set services.frontend.replicaCount=3 \
  --set services.add.replicaCount=2 \
  --set services.subtract.replicaCount=2 \
  --set services.multiply.replicaCount=2 \
  --set services.divide.replicaCount=2
```

### Example 4: Custom Image Repositories

```bash
helm install calculator ./charts/calculator-microservice-java \
  --set services.frontend.image.repository=myregistry.io/calculator/frontend \
  --set services.frontend.image.tag=v1.0.0 \
  --set services.add.image.repository=myregistry.io/calculator/add \
  --set services.add.image.tag=v1.0.0
```

### Example 5: Resource Limits

```yaml
# values-production.yaml
services:
  frontend:
    resources:
      requests:
        cpu: 100m
        memory: 256Mi
      limits:
        cpu: 500m
        memory: 512Mi
  add:
    resources:
      requests:
        cpu: 50m
        memory: 128Mi
      limits:
        cpu: 200m
        memory: 256Mi
```

```bash
helm install calculator ./charts/calculator-microservice-java -f values-production.yaml
```

## Architecture

### Service Communication Flow

1. **Frontend Service** receives requests and manages caching
2. **Math Services** perform operations with 2-second artificial delays
3. **Subtract Service** delegates to Add service (unique pattern)
4. **Redis** (optional) provides distributed caching
5. **PostgreSQL** (optional) stores request logs

### Chaos Engineering

The chart supports chaos engineering to test resilience:
- Configure globally or per-service
- Injects random failures based on percentage
- Defaults to 5% failure rate when enabled without specific percentage

## Uninstalling

```bash
helm uninstall calculator
```

To also remove PVCs:

```bash
kubectl delete pvc -l app.kubernetes.io/instance=calculator
```

## Accessing the Application

### With Port-Forward

```bash
export POD_NAME=$(kubectl get pods -l "app.kubernetes.io/component=frontend" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward $POD_NAME 8080:80
```

Visit http://localhost:8080

### With Ingress

Access via your configured ingress hostname.

## Monitoring

Check service status:

```bash
kubectl get pods -l app.kubernetes.io/instance=calculator
kubectl get svc -l app.kubernetes.io/instance=calculator
```

View logs:

```bash
# Frontend logs
kubectl logs -l app.kubernetes.io/component=frontend -f

# Specific service logs
kubectl logs -l app.kubernetes.io/component=add -f
```

## Troubleshooting

### Pods not starting

Check events:
```bash
kubectl describe pod -l app.kubernetes.io/instance=calculator
```

### Database connection issues

Verify PostgreSQL is running:
```bash
kubectl get pods -l app.kubernetes.io/component=postgres
kubectl logs -l app.kubernetes.io/component=postgres
```

### Cache not working

Verify Redis is running:
```bash
kubectl get pods -l app.kubernetes.io/component=redis
kubectl logs -l app.kubernetes.io/component=redis
```

## License

This chart and the calculator microservice are provided as-is for demonstration purposes.

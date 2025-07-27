# MongoDB Setup with Podman

This guide explains how to run MongoDB using Podman for the Kanban application across different operating systems.

## Prerequisites

### Install Podman

**Linux (Ubuntu/Debian):**

```bash
sudo apt update
sudo apt install podman podman-compose
```

**Linux (RHEL/CentOS/Fedora):**

```bash
sudo dnf install podman podman-compose
```

**macOS:**

```bash
brew install podman podman-compose
# Initialize podman machine
podman machine init
podman machine start
```

**Windows:**

1. Download Podman Desktop from https://podman.io/getting-started/installation
2. Install Podman Desktop
3. Or use WSL2 with Linux installation steps

## Running MongoDB

### Option 1: Using Podman Compose (Recommended)

1. Navigate to the project directory:

  ```bash
  cd /path/to/kjKanban
  ```

2. Start MongoDB container:

  ```bash
  podman-compose up -d
  ```

3. Stop MongoDB container:

  ```bash
  podman-compose down
  ```

4. View logs:

  ```bash
  podman-compose logs mongodb
  ```

### Option 2: Using Podman Commands Directly

1. Create a network:

   ```bash
   podman network create kanban-network
   ```

2. Create volumes:

   ```bash
   podman volume create mongodb_data
   podman volume create mongodb_config
   ```

3. Run MongoDB container:

   ```bash
   podman run -d \
     --name kanban-mongodb \
     --restart unless-stopped \
     -p 27017:27017 \
     -e MONGO_INITDB_ROOT_USERNAME=admin \
     -e MONGO_INITDB_ROOT_PASSWORD=password \
     -e MONGO_INITDB_DATABASE=kanban \
     -v mongodb_data:/data/db \
     -v mongodb_config:/data/configdb \
     --network kanban-network \
     mongo:7.0 mongod --auth
   ```

## Database Configuration

### Default Credentials

- **Username:** admin
- **Password:** password
- **Database:** kanban
- **Port:** 27017

### Application Configuration

Update your `application.yml` to connect to MongoDB:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:password@localhost:27017/kanban?authSource=admin
```

## Useful Commands

### Container Management

```bash
# Check container status
podman ps

# Stop container
podman stop kanban-mongodb

# Start container
podman start kanban-mongodb

# Remove container
podman rm kanban-mongodb

# View container logs
podman logs kanban-mongodb
```

### Volume Management

```bash
# List volumes
podman volume ls

# Inspect volume
podman volume inspect mongodb_data

# Remove volumes (WARNING: This will delete all data)
podman volume rm mongodb_data mongodb_config
```

### Network Management

```bash
# List networks
podman network ls

# Inspect network
podman network inspect kanban-network

# Remove network
podman network rm kanban-network
```

## Connecting to MongoDB Shell

```bash
# Connect to MongoDB shell
podman exec -it kanban-mongodb mongosh -u admin -p password --authenticationDatabase admin

# Or using mongosh directly (if installed locally)
mongosh mongodb://admin:password@localhost:27017/kanban?authSource=admin
```

## Troubleshooting

### Port Already in Use

If port 27017 is already in use, change the port mapping:

```bash
# Use different port (e.g., 27018)
podman run ... -p 27018:27017 ...
```

Then update your application configuration:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:password@localhost:27018/kanban?authSource=admin
```

### Permission Issues (Linux)

If you encounter permission issues:

```bash
# Add user to podman group
sudo usermod -aG podman $USER
# Logout and login again
```

### macOS Podman Machine Issues

```bash
# Reset podman machine
podman machine stop
podman machine rm
podman machine init
podman machine start
```

## Data Persistence

Data is persisted in named volumes:

- `mongodb_data`: Database files
- `mongodb_config`: Configuration files

These volumes will persist even if the container is removed, ensuring your data is safe.

## Security Notes

- Change default credentials in production
- Use environment variables for sensitive data
- Consider using secrets management for production deployments
- Restrict network access as needed

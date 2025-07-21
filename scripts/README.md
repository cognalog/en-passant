# Testing Scripts

This directory contains scripts for testing and monitoring the chess application.

## Scripts Overview

### `test-integration.sh`
Comprehensive integration testing script that:
- Builds and starts both backend and frontend services using Docker Compose
- Tests all API endpoints with various scenarios
- Verifies CORS configuration
- Tests configuration flexibility (custom ports and search depth)
- Validates frontend accessibility
- Provides colored output and detailed logging

**Usage:**
```bash
# Run with default configuration
./scripts/test-integration.sh

# Run with custom configuration
BACKEND_PORT=9000 BOT_SEARCH_DEPTH=3 ./scripts/test-integration.sh
```

### `health-check.sh`
Simple health monitoring script that:
- Checks if backend and frontend services are responding
- Verifies API functionality with a test request
- Returns appropriate exit codes for monitoring systems
- Can be used in production monitoring setups

**Usage:**
```bash
# Basic health check
./scripts/health-check.sh

# With custom ports
BACKEND_PORT=9000 FRONTEND_PORT=3000 ./scripts/health-check.sh
```

## CI/CD Integration

These scripts are used by the GitHub Actions workflows:

1. **`scala.yml`** - Basic unit testing and compilation
2. **`integration-test.yml`** - Full integration testing using these scripts

## Testing Scenarios Covered

### API Endpoint Testing
- ✅ CORS preflight requests
- ✅ Move generation from initial position
- ✅ Move generation with game history
- ✅ Board printing functionality
- ✅ Error handling for invalid requests
- ✅ Multiple concurrent requests
- ✅ Both White and Black color requests

### Service Testing
- ✅ Backend service startup and configuration
- ✅ Frontend service accessibility
- ✅ JavaScript file serving
- ✅ Service communication
- ✅ Configuration flexibility (custom ports and search depth)

### End-to-End Testing
- ✅ Complete API workflow
- ✅ Service interaction
- ✅ Docker container functionality
- ✅ Environment variable configuration

## Configuration Options

All scripts respect the following environment variables:

- `BACKEND_PORT` - Backend service port (default: 8080)
- `FRONTEND_PORT` - Frontend service port (default: 3000)
- `BOT_SEARCH_DEPTH` - AI search depth (default: 2 for tests, 4 for production)

## Requirements

- Docker and Docker Compose
- curl
- bash
- python3 (for JSON formatting)

## Output

Scripts provide:
- ✅ Green checkmarks for passed tests
- ❌ Red X marks for failed tests
- 🟡 Yellow text for informational messages
- Detailed error messages and response bodies for debugging
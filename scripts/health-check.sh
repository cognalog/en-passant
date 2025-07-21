#!/bin/bash

# Health check script for chess application services
# Returns 0 if all services are healthy, 1 otherwise

BACKEND_PORT=${BACKEND_PORT:-8080}
FRONTEND_PORT=${FRONTEND_PORT:-3000}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Chess Application Health Check${NC}"
echo "Backend Port: $BACKEND_PORT"
echo "Frontend Port: $FRONTEND_PORT"
echo ""

# Function to check service health
check_service() {
    local url=$1
    local service_name=$2
    local timeout=${3:-5}
    
    if curl -f --max-time $timeout "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $service_name is healthy${NC}"
        return 0
    else
        echo -e "${RED}✗ $service_name is not responding${NC}"
        return 1
    fi
}

# Check backend health
echo "Checking backend health..."
if check_service "http://localhost:$BACKEND_PORT/api/chess/move" "Backend API" 10; then
    backend_healthy=true
else
    backend_healthy=false
fi

# Check frontend health
echo "Checking frontend health..."
if check_service "http://localhost:$FRONTEND_PORT" "Frontend"; then
    frontend_healthy=true
else
    frontend_healthy=false
fi

# Check backend API functionality
echo "Checking backend API functionality..."
api_response=$(curl -X POST \
    -H "Content-Type: application/json" \
    -d '{"board": "", "color": "Black"}' \
    -w "%{http_code}" \
    -s \
    -o /dev/null \
    --max-time 10 \
    "http://localhost:$BACKEND_PORT/api/chess/move" 2>/dev/null)

if [ "$api_response" = "200" ]; then
    echo -e "${GREEN}✓ Backend API is functional${NC}"
    api_healthy=true
else
    echo -e "${RED}✗ Backend API is not functional (status: $api_response)${NC}"
    api_healthy=false
fi

echo ""

# Overall health status
if [ "$backend_healthy" = true ] && [ "$frontend_healthy" = true ] && [ "$api_healthy" = true ]; then
    echo -e "${GREEN}✓ All services are healthy${NC}"
    exit 0
else
    echo -e "${RED}✗ Some services are not healthy${NC}"
    exit 1
fi
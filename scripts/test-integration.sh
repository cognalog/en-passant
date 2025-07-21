#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
BACKEND_PORT=${BACKEND_PORT:-8080}
FRONTEND_PORT=${FRONTEND_PORT:-3000}
BOT_SEARCH_DEPTH=${BOT_SEARCH_DEPTH:-2}

echo -e "${YELLOW}Starting Chess Application Integration Tests${NC}"
echo "Configuration:"
echo "  Backend Port: $BACKEND_PORT"
echo "  Frontend Port: $FRONTEND_PORT"
echo "  Bot Search Depth: $BOT_SEARCH_DEPTH"
echo ""

# Function to check if a service is ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}$service_name is ready!${NC}"
            return 0
        fi
        echo "Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 2
        ((attempt++))
    done
    
    echo -e "${RED}$service_name failed to start within timeout${NC}"
    return 1
}

# Function to test API endpoint
test_api_endpoint() {
    local endpoint=$1
    local method=$2
    local data=$3
    local expected_status=${4:-200}
    local description=$5
    
    echo -e "${YELLOW}Testing: $description${NC}"
    
    if [ "$method" = "POST" ]; then
        response=$(curl -X POST \
            -H "Content-Type: application/json" \
            -d "$data" \
            -w "%{http_code}" \
            -s \
            -o /tmp/api_test_response.json \
            "$endpoint")
    else
        response=$(curl -X "$method" \
            -w "%{http_code}" \
            -s \
            -o /tmp/api_test_response.json \
            "$endpoint")
    fi
    
    if [ "$response" = "$expected_status" ]; then
        echo -e "${GREEN}✓ $description - PASSED${NC}"
        if [ -f /tmp/api_test_response.json ]; then
            echo "Response:"
            cat /tmp/api_test_response.json | python3 -m json.tool 2>/dev/null || cat /tmp/api_test_response.json
        fi
        echo ""
        return 0
    else
        echo -e "${RED}✗ $description - FAILED${NC}"
        echo "Expected status: $expected_status, Got: $response"
        if [ -f /tmp/api_test_response.json ]; then
            echo "Response:"
            cat /tmp/api_test_response.json
        fi
        echo ""
        return 1
    fi
}

# Clean up function
cleanup() {
    echo -e "${YELLOW}Cleaning up...${NC}"
    docker-compose down > /dev/null 2>&1 || true
    rm -f /tmp/api_test_response.json /tmp/frontend_test.html
}

# Set trap for cleanup
trap cleanup EXIT

# Start the application
echo -e "${YELLOW}Building and starting services...${NC}"
echo "Building Docker images (this may take a while)..."
if ! docker-compose build; then
    echo -e "${RED}Docker build failed. Check logs above.${NC}"
    exit 1
fi

echo "Starting services..."
if ! docker-compose up -d; then
    echo -e "${RED}Failed to start services. Check logs above.${NC}"
    exit 1
fi

# Wait for services to be ready
wait_for_service "http://localhost:$BACKEND_PORT/api/chess/move" "Backend API" || exit 1
wait_for_service "http://localhost:$FRONTEND_PORT" "Frontend" || exit 1

echo -e "${GREEN}All services are ready! Starting tests...${NC}"
echo ""

# Test backend API endpoints
echo -e "${YELLOW}=== Testing Backend API Endpoints ===${NC}"

# Test CORS preflight
test_api_endpoint \
    "http://localhost:$BACKEND_PORT/api/chess/move" \
    "OPTIONS" \
    "" \
    200 \
    "CORS preflight request"

# Test move endpoint with initial position
test_api_endpoint \
    "http://localhost:$BACKEND_PORT/api/chess/move" \
    "POST" \
    '{"board": "", "color": "Black"}' \
    200 \
    "Bot move from initial position"

# Test move endpoint with some moves played
test_api_endpoint \
    "http://localhost:$BACKEND_PORT/api/chess/move" \
    "POST" \
    '{"board": "e2e4 e7e5", "color": "Black"}' \
    200 \
    "Bot move after e2e4 e7e5"

# Test printBoard endpoint
test_api_endpoint \
    "http://localhost:$BACKEND_PORT/api/chess/printBoard" \
    "POST" \
    '{"board": ""}' \
    200 \
    "Print board for initial position"

# Test printBoard endpoint with moves
test_api_endpoint \
    "http://localhost:$BACKEND_PORT/api/chess/printBoard" \
    "POST" \
    '{"board": "e2e4 e7e5"}' \
    200 \
    "Print board after e2e4 e7e5"

echo -e "${YELLOW}=== Testing Frontend Accessibility ===${NC}"

# Test frontend main page
frontend_response=$(curl -w "%{http_code}" -s -o /tmp/frontend_test.html "http://localhost:$FRONTEND_PORT")
if [ "$frontend_response" = "200" ]; then
    echo -e "${GREEN}✓ Frontend main page loads successfully${NC}"
else
    echo -e "${RED}✗ Frontend main page failed to load (status: $frontend_response)${NC}"
    exit 1
fi

# Test JavaScript files
if curl -f "http://localhost:$FRONTEND_PORT/main.js" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Frontend JavaScript files are accessible${NC}"
else
    echo -e "${RED}✗ Frontend JavaScript files are not accessible${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}=== Testing Configuration Flexibility ===${NC}"

# Stop current services
docker-compose down

# Test with different configuration
export BACKEND_PORT=9000
export BOT_SEARCH_DEPTH=1

echo "Testing with Backend Port: $BACKEND_PORT, Search Depth: $BOT_SEARCH_DEPTH"

# Start with new configuration
docker-compose up -d

# Wait for service to be ready on new port
wait_for_service "http://localhost:9000/api/chess/move" "Backend API (port 9000)" || exit 1

# Test the service on new port
test_api_endpoint \
    "http://localhost:9000/api/chess/move" \
    "POST" \
    '{"board": "", "color": "Black"}' \
    200 \
    "Bot move on custom port (9000)"

echo ""
echo -e "${GREEN}=== All Integration Tests Passed! ===${NC}"
echo -e "${GREEN}✓ Backend API endpoints working correctly${NC}"
echo -e "${GREEN}✓ Frontend serving files correctly${NC}"
echo -e "${GREEN}✓ Configuration flexibility verified${NC}"
echo -e "${GREEN}✓ Services start and communicate properly${NC}"
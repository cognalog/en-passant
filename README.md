# en-passant

A chess bot framework using various search algorithms
combined with modular heuristics.

## System requirements

* Docker
* Docker Compose

## Running the application

```shell
# Build and run both frontend and backend using Docker Compose
docker-compose up --build

# The application will be available at http://localhost:3000
```

## Configuration

You can customize the backend port and bot search depth using environment variables:

### Using Docker Compose

Create a `.env` file in the project root (see `.env.example` for reference):

```shell
# Backend port (default: 8080)
BACKEND_PORT=9000

# Bot search depth (default: 4)
# Higher values make the bot stronger but slower
BOT_SEARCH_DEPTH=6
```

Then run:
```shell
docker-compose up --build
```

### Using Docker directly

```shell
# Run with custom port and search depth
docker build -t chess-app .
docker run -p 9000:9000 -e BACKEND_PORT=9000 -e BOT_SEARCH_DEPTH=6 chess-app
```

### Environment Variables

- `BACKEND_PORT`: Port for the backend server (default: 8080)
- `BOT_SEARCH_DEPTH`: Search depth for the chess AI (default: 4)
  - Higher values (5-8) make the bot stronger but significantly slower
  - Lower values (2-3) make the bot faster but weaker

## Playing against the bot

1. Open http://localhost:3000 in your web browser
2. You play as White, and the bot plays as Black
3. Make your move by dragging and dropping pieces
4. The bot will automatically respond with its move
5. The game status is shown below the board

## Features

- Beautiful, responsive chess GUI
- Automatic move validation
- Real-time game status updates
- Visual move highlighting
- Automatic pawn promotion to queen
- Support for all standard chess moves including castling and en passant

## Development

The project consists of two main components:

### Frontend (Scala.js)
- Located in `/frontend`
- Uses Scala.js for type-safe JavaScript
- Chess.js for move validation
- Chessboard.js for the UI

### Backend (Scala)
- Located in `/backend`
- Implements the chess bot logic
- Various search algorithms and heuristics
- RESTful API for move generation

## Testing

The project includes comprehensive testing at multiple levels:

### Unit Tests
```shell
# Run all tests
sbt test

# Run only backend tests
sbt backend/test

# Run only frontend tests
sbt frontend/test
```

### Integration Tests
```shell
# Run integration tests locally (requires Docker)
./scripts/test-integration.sh

# Run with custom configuration
BACKEND_PORT=9000 BOT_SEARCH_DEPTH=3 ./scripts/test-integration.sh
```

### CI/CD
The project includes two GitHub Actions workflows:

1. **Scala CI** (`scala.yml`) - Runs unit tests on every push/PR
2. **Integration Tests** (`integration-test.yml`) - Comprehensive testing including:
   - Backend and frontend service startup
   - API endpoint functionality testing
   - CORS configuration verification
   - Configuration flexibility testing
   - End-to-end service communication

**Note**: If you encounter compilation issues in CI, the workflows are configured to:
- Use JDK 17 (compatible with Scala 2.13.4)
- Compile before testing (proper order)
- Use compatible dependency versions (ScalaTest 3.2.15, ScalaMock 5.2.0)
- Cache dependencies for faster builds
- Proper HTTP test imports (HttpMethods, SprayJsonSupport)
- Compatible actor system usage (no typed/untyped mixing)

### Troubleshooting
```shell
# Test compilation locally (dry-run)
./scripts/test-compile.sh

# Test compilation with sbt (requires sbt installation)
sbt backend/compile
sbt backend/test
```

### API Testing
The backend provides the following REST endpoints:

- `POST /api/chess/move` - Get the bot's next move
  ```json
  {
    "board": "e2e4 e7e5",
    "color": "Black"
  }
  ```

- `POST /api/chess/printBoard` - Get board representation
  ```json
  {
    "board": "e2e4 e7e5"
  }
  ```

- `OPTIONS /api/chess/*` - CORS preflight requests
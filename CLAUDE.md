# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a chess bot framework built with Scala/Scala.js, featuring a backend AI engine and a frontend web interface. The project implements various search algorithms (minimax with alpha-beta pruning) combined with modular chess position evaluators.

## Architecture

### Backend (Scala)
- **Location**: `/backend/src/main/scala/`
- **Framework**: Akka HTTP with spray-json for REST API
- **Core Components**:
  - `model/`: Chess game logic (Board, Piece classes, moves)
  - `ai/`: Chess AI engine with search algorithms and evaluators
  - `actor/`: Game management and player abstractions
  - `api/`: REST service layer (ChessService, JsonFormats)

### Frontend (Scala.js)
- **Location**: `/frontend/src/main/scala/enpassant/`
- **Framework**: Laminar for reactive UI
- **External Dependencies**: Chess.js (move validation), Chessboard.js (UI)
- **Components**: GameUI, ChessBoard wrapper, MoveHandler, GameState management

## Common Development Commands

### Testing
```bash
# Run all tests (both backend and frontend)
sbt test

# Backend tests only
sbt backend/test

# Frontend tests only  
sbt frontend/test

# Integration tests (requires Docker)
./scripts/test-integration.sh

# Test compilation check (dry-run)
./scripts/test-compile.sh

# Test structure verification
./scripts/verify-tests.sh
```

### Development
```bash
# Compile backend
sbt backend/compile

# Compile frontend to JavaScript
sbt frontend/fastLinkJS

# Run with Docker Compose
docker-compose up --build

# Custom configuration example
BACKEND_PORT=9000 FRONTEND_PORT=4000 BOT_SEARCH_DEPTH=6 docker-compose up --build
```

## Key Implementation Details

### Board Representation
- `StandardBoard.StartingPosition` for initial chess position
- `Board.standardFromMoveStrings(moves: Seq[String])` to create board from move history
- Moves use standard algebraic notation (e.g., "e4", "Nf3")

### AI Engine
- `ABPruningMinimax` is the primary search algorithm
- `GeneralEvaluator` combines multiple heuristics:
  - `CheckmateEvaluator`: Terminal position evaluation
  - `PieceScoreEvaluator`: Material balance (coefficient: 3x)
  - `PieceDevelopmentEvaluator`: Piece activity and development
  - `KingSafetyEvaluator`: King safety assessment

### API Endpoints
- `POST /api/chess/move`: Get bot's next move
- `POST /api/chess/printBoard`: Get board representation
- CORS enabled for localhost:3000 (configurable via CORS_ALLOWED_ORIGIN)

### Environment Configuration
- `BACKEND_PORT`: Backend server port (default: 8080)
- `FRONTEND_PORT`: Frontend server port (default: 3000)
- `BOT_SEARCH_DEPTH`: AI search depth (default: 4, range: 1-8)
- Higher search depths improve AI strength but increase response time
- Frontend automatically connects to the configured backend port

### Testing Patterns
- Backend tests use ScalaTest with ScalaMock
- Test files follow `*Test.scala` naming convention
- Integration tests verify full Docker stack functionality
- GetNextMove expects Board objects, not move strings
- Use `StandardBoard.StartingPosition` for initial board states
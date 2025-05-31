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
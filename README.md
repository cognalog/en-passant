# en-passant

A chess bot framework using various search algorithms
combined with modular heuristics.

## System requirements

* Docker

## Server turn-up

```shell
# Build and run the Docker container
docker build -t en-passant .
docker run -p 8080:8080 en-passant

# The server will be available at http://localhost:8080
```

## API Endpoints

The server exposes the following REST endpoints:

### POST /api/chess/move

Get the bot's next move for a given board position. 

```powershell
# Example using PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/chess/move" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"board":"","color":"White"}'
```

Request body:
- `board`: String representation of the board position. Empty string (`""`) for initial position, or space-separated moves for a specific position
- `color`: The color to move ("White" or "Black")

Returns:
- A move in standard chess notation (e.g. "Ne2e4" for Knight from e2 to e4)

### POST /api/chess/printBoard

Get a string representation of any board position.

```powershell
# Example using PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/chess/printBoard" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"board":""}'
```

Request body:
- `board`: String representation of the board position. Empty string (`""`) for initial position, or space-separated moves for a specific position

Returns:
- A string representation of the current board state

## Examples

1. Get bot's move from starting position:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chess/move" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"board":"","color":"White"}'
```

2. Print the starting position:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chess/printBoard" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"board":""}'
```

3. Get bot's move after a specific sequence of moves:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chess/move" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"board":"e2e4 e7e5","color":"White"}'
```
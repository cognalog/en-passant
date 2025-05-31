// Initialize the chess game
const game = new Chess();
let moveHistory = [];

// Configure the board
const config = {
    draggable: true,
    position: 'start',
    pieceTheme: 'https://chessboardjs.com/img/chesspieces/alpha/{piece}.png',
    onDragStart: onDragStart,
    onDrop: onDrop,
    onSnapEnd: onSnapEnd
};

const board = Chessboard('board', config);

function onDragStart(source, piece) {
    // Only allow white pieces to be dragged when it's white's turn
    // and black pieces when it's black's turn
    if (game.game_over()) return false;

    if ((game.turn() === 'w' && piece.search(/^b/) !== -1) ||
        (game.turn() === 'b' && piece.search(/^w/) !== -1)) {
        return false;
    }
}

function onDrop(source, target) {
    // Try to make the move
    const move = game.move({
        from: source,
        to: target,
        promotion: 'q' // Always promote to queen for simplicity
    });

    // If illegal move, snap back
    if (move === null) return 'snapback';

    // Add move to history in standard algebraic notation
    moveHistory.push(move.san);

    // Make computer move after a short delay
    if (!game.game_over()) {
        setTimeout(makeBotMove, 250);
    }
    updateStatus();
}

function onSnapEnd() {
    board.position(game.fen());
}

async function makeBotMove() {
    try {
        const response = await fetch('/api/chess/move', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                board: moveHistory.join(' '),
                color: 'Black'  // Bot always plays as Black
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        const moveStr = data.move;

        if (moveStr) {
            // Handle castling moves directly
            if (moveStr === "O-O" || moveStr === "O-O-O") {
                const move = game.move(moveStr);
                if (move) {
                    moveHistory.push(move.san);
                    board.position(game.fen());
                    updateStatus();
                }
                return;
            }

            // For normal moves, extract just the essential parts:
            // 1. The piece letter (if any)
            // 2. The destination square
            // 3. Any capture or promotion information
            const match = moveStr.match(/([NBRQK])?([a-h][1-8])?([a-h][1-8])(=[NBRQ])?/);
            if (match) {
                const [_, piece = "", , dest, promotion = ""] = match;
                const simplifiedMove = piece + dest + promotion;

                const move = game.move(simplifiedMove);
                if (move) {
                    moveHistory.push(move.san);
                    board.position(game.fen());
                    updateStatus();
                }
            }
        }
    } catch (error) {
        console.error('Error making bot move:', error);
        document.getElementById('status').textContent = 'Error: Failed to get bot move';
    }
}

function updateStatus() {
    let status = '';

    if (game.in_checkmate()) {
        status = 'Game over, ' + (game.turn() === 'w' ? 'black' : 'white') + ' wins by checkmate.';
    } else if (game.in_draw()) {
        status = 'Game over, drawn position';
    } else {
        status = (game.turn() === 'w' ? 'White' : 'Black') + ' to move';
        if (game.in_check()) {
            status += ', ' + (game.turn() === 'w' ? 'white' : 'black') + ' is in check';
        }
    }

    document.getElementById('status').textContent = status;
}

// Event listeners for buttons
document.getElementById('startBtn').addEventListener('click', () => {
    game.reset();
    board.start();
    moveHistory = [];
    updateStatus();
});

document.getElementById('undoBtn').addEventListener('click', () => {
    if (moveHistory.length >= 2) {
        game.undo();
        game.undo(); // Undo twice to undo both player and computer moves
        moveHistory.pop(); // Remove computer's move
        moveHistory.pop(); // Remove player's move
        board.position(game.fen());
        updateStatus();
    }
}); 
package enpassant

import org.scalatest.Suites

/**
 * Comprehensive test suite for all frontend components.
 * 
 * This test suite includes unit tests for all modularized components:
 * - GameState: Game state management and move history
 * - ChessBoard: Board management and Chess.js integration  
 * - MoveHandler: Move processing and validation
 * - GameStatusManager: Game status updates and display
 * - GameUI: User interface components and logic
 * - ChessUtils: Chess utilities and FEN parsing
 * 
 * Run all tests with: sbt frontend/test
 * Run specific test class with: sbt "frontend/testOnly *GameStateTest"
 */
class AllTests extends Suites(
  new GameStateTest,
  new ChessBoardTest,
  new MoveHandlerTest,
  new GameStatusManagerTest,
  new GameUITest,
  new ChessUtilsTest
)
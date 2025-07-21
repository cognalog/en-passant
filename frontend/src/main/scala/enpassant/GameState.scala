package enpassant

import com.raquo.laminar.api.L._

case class ChessMove(
    from: String,
    to: String,
    san: String,
    promotion: Option[String] = None
)

object GameState {
  private var _moveHistory: List[ChessMove] = List.empty
  private val startPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

  // Initialize signals
  val moveHistorySignal: Var[List[ChessMove]] = Var(List.empty[ChessMove])
  val currentMoveIndexSignal: Var[Int] = Var(-1) // -1 means latest move
  val showWarningSignal: Var[Boolean] = Var(false)

  def moveHistory: List[ChessMove] = _moveHistory
  
  def addMove(move: ChessMove): Unit = {
    _moveHistory = _moveHistory :+ move
    moveHistorySignal.set(_moveHistory)
  }

  def clearHistory(): Unit = {
    _moveHistory = List.empty
    moveHistorySignal.set(List.empty)
    currentMoveIndexSignal.set(-1)
  }

  def truncateHistoryTo(index: Int): Unit = {
    _moveHistory = _moveHistory.take(index + 1)
    moveHistorySignal.set(_moveHistory)
  }

  def resetToLatest(): Unit = {
    currentMoveIndexSignal.set(-1)
  }

  def setCurrentMoveIndex(index: Int): Unit = {
    currentMoveIndexSignal.set(index)
  }

  def getCurrentMoveIndex: Int = currentMoveIndexSignal.now()

  def getStartPosition: String = startPosition

  // Track if we're viewing a historical position
  def isViewingHistory: Boolean = {
    val currentIndex = currentMoveIndexSignal.now()
    currentIndex >= 0 && currentIndex < _moveHistory.length - 1
  }

  // Helper method to format moves in pairs for display
  def formatMovePairs(moves: List[ChessMove]): List[(Int, String, String)] = {
    moves.map(_.san).grouped(2).toList.zipWithIndex.map { case (pair, idx) =>
      val moveNumber = idx + 1
      val whitePart = pair.headOption.getOrElse("")
      val blackPart = pair.lift(1).getOrElse("")
      (moveNumber, whitePart, blackPart)
    }
  }
}
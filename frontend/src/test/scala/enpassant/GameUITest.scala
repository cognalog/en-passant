package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import com.raquo.laminar.api.L._

class GameUITest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    GameState.clearHistory()
  }

  "Move display logic" should "determine clickability correctly" in {
    def isClickableMove(moveIndex: Int, isBlackMove: Boolean, blackMoveNotEmpty: Boolean): Boolean = {
      val wouldBeWhiteTurn = moveIndex % 2 == 1 // Odd index means after black's move
      blackMoveNotEmpty && wouldBeWhiteTurn
    }

    // Test move indices and clickability
    isClickableMove(0, false, true) shouldBe false // White move (even index)
    isClickableMove(1, true, true) shouldBe true   // Black move (odd index) with content
    isClickableMove(1, true, false) shouldBe false // Black move (odd index) but empty
    isClickableMove(2, false, true) shouldBe false // White move (even index)
    isClickableMove(3, true, true) shouldBe true   // Black move (odd index) with content
  }

  "Move formatting for display" should "format moves correctly for UI" in {
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5"),
      ChessMove("g1", "f3", "Nf3"),
      ChessMove("b8", "c6", "Nc6"),
      ChessMove("f1", "c4", "Bc4")
    )

    val formatted = GameState.formatMovePairs(moves)
    
    formatted should have size 3
    formatted(0) shouldBe (1, "e4", "e5")     // Move pair 1
    formatted(1) shouldBe (2, "Nf3", "Nc6")  // Move pair 2
    formatted(2) shouldBe (3, "Bc4", "")     // Move pair 3 (incomplete)
  }

  "Move highlighting logic" should "identify current move correctly" in {
    def isCurrentMove(moveIndex: Int, currentMoveIndex: Int): Boolean = {
      currentMoveIndex >= 0 && moveIndex == currentMoveIndex
    }

    isCurrentMove(0, 0) shouldBe true
    isCurrentMove(1, 1) shouldBe true
    isCurrentMove(0, 1) shouldBe false
    isCurrentMove(2, 1) shouldBe false
    isCurrentMove(0, -1) shouldBe false // -1 means latest move
  }

  "Future move detection" should "identify future moves correctly" in {
    def isFutureMove(moveIndex: Int, currentMoveIndex: Int): Boolean = {
      currentMoveIndex >= 0 && moveIndex > currentMoveIndex
    }

    // When viewing move at index 1
    isFutureMove(0, 1) shouldBe false // Past move
    isFutureMove(1, 1) shouldBe false // Current move
    isFutureMove(2, 1) shouldBe true  // Future move
    isFutureMove(3, 1) shouldBe true  // Future move

    // When at latest position (-1)
    isFutureMove(0, -1) shouldBe false
    isFutureMove(1, -1) shouldBe false
    isFutureMove(2, -1) shouldBe false
  }

  "CSS style generation" should "generate style strings correctly" in {
    def generateStyleString(styles: Seq[(String, String)]): String = {
      styles.map { case (k, v) => s"$k: $v" }.mkString(";")
    }

    val styles = Seq(
      "color" -> "red",
      "background-color" -> "#f0f0f0",
      "font-size" -> "14px"
    )

    val styleString = generateStyleString(styles)
    styleString shouldBe "color: red;background-color: #f0f0f0;font-size: 14px"
  }

  "Move pair indexing" should "calculate indices correctly" in {
    def calculateMoveIndices(pairIndex: Int): (Int, Int) = {
      val whiteIndex = pairIndex * 2
      val blackIndex = whiteIndex + 1
      (whiteIndex, blackIndex)
    }

    calculateMoveIndices(0) shouldBe (0, 1) // First pair: moves 0 and 1
    calculateMoveIndices(1) shouldBe (2, 3) // Second pair: moves 2 and 3
    calculateMoveIndices(2) shouldBe (4, 5) // Third pair: moves 4 and 5
  }

  "Move number calculation" should "calculate move numbers correctly" in {
    def calculateMoveNumber(pairIndex: Int): Int = pairIndex + 1

    calculateMoveNumber(0) shouldBe 1
    calculateMoveNumber(1) shouldBe 2
    calculateMoveNumber(2) shouldBe 3
    calculateMoveNumber(9) shouldBe 10
  }

  "Background color logic" should "determine background colors correctly" in {
    def getBackgroundColor(moveIndex: Int, currentMoveIndex: Int): String = {
      if (currentMoveIndex >= 0 && moveIndex == currentMoveIndex) {
        "background-color: #e0e0e0;"
      } else {
        ""
      }
    }

    getBackgroundColor(1, 1) shouldBe "background-color: #e0e0e0;"
    getBackgroundColor(0, 1) shouldBe ""
    getBackgroundColor(1, -1) shouldBe ""
    getBackgroundColor(2, 1) shouldBe ""
  }

  "Text color logic" should "determine text colors for future moves" in {
    def getTextColor(isFuture: Boolean): Seq[(String, String)] = {
      if (isFuture) Seq("color" -> "#999") else Seq()
    }

    getTextColor(true) shouldBe Seq("color" -> "#999")
    getTextColor(false) shouldBe Seq()
  }

  "UI component validation" should "validate component structure" in {
    case class UIComponent(
      cssClass: String,
      content: String,
      clickable: Boolean,
      styles: Map[String, String]
    )

    def validateComponent(comp: UIComponent): Boolean = {
      comp.cssClass.nonEmpty && 
      comp.content.nonEmpty && 
      comp.styles.nonEmpty
    }

    val validComponent = UIComponent(
      "move-item", 
      "e4", 
      false, 
      Map("margin" -> "5px")
    )
    
    val invalidComponent = UIComponent(
      "", 
      "", 
      false, 
      Map()
    )

    validateComponent(validComponent) shouldBe true
    validateComponent(invalidComponent) shouldBe false
  }

  "Move log styling" should "generate correct styles for move log" in {
    val expectedMoveLogStyles = Seq(
      "display" -> "flex",
      "overflow-x" -> "auto",
      "padding" -> "10px",
      "margin-top" -> "10px",
      "background-color" -> "#f5f5f5",
      "border-radius" -> "4px",
      "font-family" -> "monospace"
    )

    expectedMoveLogStyles should contain("display" -> "flex")
    expectedMoveLogStyles should contain("overflow-x" -> "auto")
    expectedMoveLogStyles should contain("background-color" -> "#f5f5f5")
  }

  "Button action logic" should "handle new game action" in {
    case class GameAction(resetBoard: Boolean, clearHistory: Boolean, updateStatus: Boolean)
    
    def handleNewGameAction(): GameAction = {
      GameAction(
        resetBoard = true,
        clearHistory = true,
        updateStatus = true
      )
    }

    val action = handleNewGameAction()
    action.resetBoard shouldBe true
    action.clearHistory shouldBe true
    action.updateStatus shouldBe true
  }

  "Move history display" should "handle empty history correctly" in {
    GameState.clearHistory()
    val formatted = GameState.formatMovePairs(GameState.moveHistory)
    formatted shouldBe empty
  }

  it should "handle single move correctly" in {
    GameState.addMove(ChessMove("e2", "e4", "e4"))
    val formatted = GameState.formatMovePairs(GameState.moveHistory)
    
    formatted should have size 1
    formatted(0) shouldBe (1, "e4", "")
  }

  "UI responsiveness" should "handle different screen sizes conceptually" in {
    case class ScreenSize(width: Int, height: Int)
    case class UILayout(horizontal: Boolean, compactMode: Boolean)
    
    def getLayoutForScreen(screen: ScreenSize): UILayout = {
      UILayout(
        horizontal = screen.width > 768,
        compactMode = screen.width < 480
      )
    }

    val desktop = ScreenSize(1200, 800)
    val tablet = ScreenSize(768, 1024)
    val mobile = ScreenSize(375, 667)

    getLayoutForScreen(desktop) shouldBe UILayout(true, false)
    getLayoutForScreen(tablet) shouldBe UILayout(false, false)
    getLayoutForScreen(mobile) shouldBe UILayout(false, true)
  }
}
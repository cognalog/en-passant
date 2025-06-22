package enpassant

import org.scalajs.dom
import scala.scalajs.js
import scala.collection.mutable

case class AnimatedPiece(
    element: dom.html.Image,
    x: Double,
    y: Double,
    vx: Double,
    vy: Double,
    rotation: Double = 0,
    rotationSpeed: Double = 0
)

object CaptureAnimation {
  
  case class PhysicsConfig(
      gravity: Double = 0.5,
      horizontalSpeed: Double = 5.3,
      verticalSpeed: Double = -8.0,
      rotationSpeedRange: Double = 10.0
  )
  
  case class PieceImageConfig(
      baseUrl: String = "https://chessboardjs.com/img/chesspieces/alpha",
      sizeFactor: Double = 0.8
  )
  
  private val animatedPieces = mutable.ArrayBuffer[AnimatedPiece]()
  private var animationFrameId: Option[Int] = None
  private val physics = PhysicsConfig()
  private val imageConfig = PieceImageConfig()
  
  def animateCapture(
      capturedPiece: String,
      fromSquare: String,
      toSquare: String
  ): Unit = {
    // Get board element position
    val boardElement = dom.document.getElementById("board")
    if (boardElement == null) return
    
    val boardRect = boardElement.getBoundingClientRect()
    val squareSize = boardRect.width / 8
    
    // Calculate positions
    val fromFile = fromSquare.charAt(0) - 'a'
    val fromRank = 8 - fromSquare.charAt(1).asDigit
    val toFile = toSquare.charAt(0) - 'a'
    val toRank = 8 - toSquare.charAt(1).asDigit
    
    // Calculate center positions of squares
    val fromX = boardRect.left + (fromFile + 0.5) * squareSize
    val fromY = boardRect.top + (fromRank + 0.5) * squareSize
    val toX = boardRect.left + (toFile + 0.5) * squareSize
    val toY = boardRect.top + (toRank + 0.5) * squareSize
    
    // Create captured piece element
    val pieceElement = dom.document.createElement("img").asInstanceOf[dom.html.Image]
    // Convert piece format from "wp" to "wP" for ChessboardJS
    val pieceCode = if (capturedPiece.length == 2) {
      val color = capturedPiece.charAt(0)
      val piece = capturedPiece.charAt(1).toUpper
      s"$color$piece"
    } else capturedPiece
    pieceElement.src = s"${imageConfig.baseUrl}/${pieceCode}.png"
    pieceElement.style.position = "fixed"
    pieceElement.style.width = s"${squareSize * imageConfig.sizeFactor}px"
    pieceElement.style.height = s"${squareSize * imageConfig.sizeFactor}px"
    pieceElement.style.left = s"${toX - squareSize * imageConfig.sizeFactor / 2}px"
    pieceElement.style.top = s"${toY - squareSize * imageConfig.sizeFactor / 2}px"
    pieceElement.style.zIndex = "1000"
    pieceElement.style.pointerEvents = "none"
    
    dom.document.body.appendChild(pieceElement)
    
    // Calculate initial velocity based on capture direction
    val dx = toX - fromX
    val vx = if (dx < 0) -physics.horizontalSpeed else physics.horizontalSpeed
    val vy = physics.verticalSpeed
    
    // Random rotation speed
    val rotationSpeed = (Math.random() - 0.5) * physics.rotationSpeedRange
    
    val halfSize = squareSize * imageConfig.sizeFactor / 2
    animatedPieces += AnimatedPiece(
      element = pieceElement,
      x = toX - halfSize,
      y = toY - halfSize,
      vx = vx,
      vy = vy,
      rotation = 0,
      rotationSpeed = rotationSpeed
    )
    
    // Start animation if not already running
    if (animationFrameId.isEmpty) {
      startAnimation()
    }
  }
  
  private def startAnimation(): Unit = {
    def animate(): Unit = {
      // Update each piece
      animatedPieces.zipWithIndex.foreach { case (piece, index) =>
        // Apply gravity
        val newVy = piece.vy + physics.gravity
        
        // Update position
        val newX = piece.x + piece.vx
        val newY = piece.y + newVy
        
        // Update rotation
        val newRotation = piece.rotation + piece.rotationSpeed
        
        // Apply transformations
        piece.element.style.left = s"${newX}px"
        piece.element.style.top = s"${newY}px"
        piece.element.style.transform = s"rotate(${newRotation}deg)"
        
        // Update piece state
        animatedPieces(index) = piece.copy(
          x = newX,
          y = newY,
          vy = newVy,
          rotation = newRotation
        )
      }
      
      // Remove pieces that are off-screen
      val viewport = dom.window
      val toRemove = animatedPieces.zipWithIndex.collect {
        case (piece, index) if piece.y > viewport.innerHeight + 100 || 
                              piece.x < -100 || 
                              piece.x > viewport.innerWidth + 100 =>
          index
      }
      
      // Remove in reverse order to maintain indices
      toRemove.reverse.foreach { index =>
        animatedPieces(index).element.remove()
        animatedPieces.remove(index)
      }
      
      // Continue animation if pieces remain
      if (animatedPieces.nonEmpty) {
        animationFrameId = Some(dom.window.requestAnimationFrame(_ => animate()))
      } else {
        animationFrameId = None
      }
    }
    
    animationFrameId = Some(dom.window.requestAnimationFrame(_ => animate()))
  }
}
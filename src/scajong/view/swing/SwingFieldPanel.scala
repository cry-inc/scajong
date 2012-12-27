package scajong.view.swing

import scajong.model._
import scajong.util._
import scajong.controller._
import swing._
import swing.event._
import javax.imageio.ImageIO
import java.io.File
import java.awt.event._

object SwingFieldPanel {
  val CellWidth = 30
  val CellHeight = 20
  val TileImageWidth = 75
  val TileImageHeight= 95
}

case class TileClickedEvent(val tile:Tile) extends Event

class SwingFieldPanel(val controller:Controller) extends Panel with SimpleSubscriber {
  private var images = Map[String, Image]()
  private var showHint = false
  private var showMoveables = false
  private var hint:TilePair = null
  private var selectedTile:Tile = null
  private var noMoves = false
  updateSize
  loadImages

  listenTo(mouse.clicks)

  reactions += {
    case e: MouseReleased => mouseReleasedHandler(e)
  }

  def updateSize {
    preferredSize = new Dimension(
      controller.fieldWidth * SwingFieldPanel.CellWidth, 
      controller.fieldHeight * SwingFieldPanel.CellHeight)
  }

  notificationProcessor = {
    case TilesRemovedNotification(tiles) => repaint
    case TileSelectedNotification(tile) => selectedTile = tile; repaint
    case CreatedGameNotification() => noMoves = false; repaint
    case ScrambledNotification() => noMoves = false; repaint
    case StartHintNotification(hintPair) => showHint = true; hint = hintPair; repaint
    case StopHintNotification() => showHint = false; repaint
    case StartMoveablesNotification() => showMoveables = true; repaint
    case StopMoveablesNotification() => showMoveables = false; repaint
    case NoFurtherMovesNotification() => noMoves = true; repaint
  }

  def loadImages {
    val specials = Array("disabled", "selected", "empty", "tile", "hint")
    for (name <- specials)
      images += name -> ImageIO.read(new File("tiles/" + name + ".png"))
  }

  def loadTileImage(name:String) {
    images += name -> ImageIO.read(new File("tiles/" + name + ".png"))
  }

  def findTile(p:swing.Point) : Tile = {
    val tiles = controller.sortedTiles.reverse
    for (tile <- tiles) {
      val x = tile.x * SwingFieldPanel.CellWidth - 5
      val y = tile.y * SwingFieldPanel.CellHeight - 5 - 5 * tile.z
      val rect = new Rectangle(x, y, SwingFieldPanel.TileImageWidth, SwingFieldPanel.TileImageHeight)
      if (rect.contains(p)) {
        return tile
      }
    }  
    return null
  }

  def mouseReleasedHandler(e:event.MouseReleased) {
    if (e.peer.getButton == MouseEvent.BUTTON1) {
      val tile = findTile(e.point)
      if (tile != null)
        publish(new TileClickedEvent(tile))
    }
  }

  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, size.width, size.height)
    if (noMoves) {
      g.setColor(new Color(255, 0, 0))
      g.drawString("No further moves. You should scramble!", 10, 20)
    }
    val tiles = controller.sortedTiles
    for (tile <- tiles) {
      drawTile(g, tile)
    }
  }

  def drawTile(g:Graphics2D, tile:Tile) {
    if (!images.contains(tile.tileType.name))
      loadTileImage(tile.tileType.name)
    val x = tile.x * SwingFieldPanel.CellWidth - 5
    val y = tile.y * SwingFieldPanel.CellHeight - 5 - 5 * tile.z
    g.drawImage(images("tile"), x, y, null)
    g.drawImage(images(tile.tileType.name), x, y, null)
    if (showMoveables && !controller.canMove(tile)) {
      g.drawImage(images("disabled"), x, y, null)
    } 
    if (showHint && (tile == hint.tile1 || tile == hint.tile2)) {
      g.drawImage(images("hint"), x, y, null)
    }
    if (tile == selectedTile) {
      g.drawImage(images("selected"), x, y, null)
    }
  }
}
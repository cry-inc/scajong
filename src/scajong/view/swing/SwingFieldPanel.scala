package scajong.view.swing

import scajong.model._
import scajong.util._

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
case class HintEvent() extends Event
case class MoveablesEvent() extends Event

class SwingFieldPanel(val game:Game, name:String) extends Panel with SimpleSubscriber {
  private var images = Map[String, Image]()
  private var showHint = false
  private var showMoveable = false
  private val hintTimer = new ScalaTimer(1000, "hint")
  private val moveablesTimer = new ScalaTimer(5000, "moveables")
  updateSize
  loadImages

  listenTo(mouse.clicks)
  listenTo(hintTimer)
  listenTo(moveablesTimer)
  game.addSubscriber(this)

  reactions += {
    case e: MouseReleased => mouseReleasedHandler(e)
    case e: TimerEvent => handleTimers(e.name)
  }

  def updateSize {
    preferredSize = new Dimension(
      game.width * SwingFieldPanel.CellWidth, 
      game.height * SwingFieldPanel.CellHeight)
  }

  def handleTimers(name:String) {
    if (name == "hint") {
      showHint = false
      hintTimer.stop
    } else if (name == "moveables") {
      showMoveable = false
      moveablesTimer.stop
    }
    repaint
  }

  def enableMoveables {
    publish(new MoveablesEvent)
    moveablesTimer.start
    showMoveable = true
    repaint
  }

  def enableHint {
    publish(new HintEvent)
    hintTimer.start
    showHint = true
    repaint
  }

  override def processNotification(sn:SimpleNotification) {
    sn match {
      case TilesChangedNotification() => repaint
      case SelectedTileNotification(tile) => repaint
      case CreatedGameNotification() => repaint
      case ScrambledNotification() => repaint
      case NoFurtherMovesNotification() => // TODO: Ask for scramble
      case _ => // Do Nothing
    }
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
    val tiles = game.sortedTiles.reverse
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
    } else if (e.peer.getButton == MouseEvent.BUTTON2) {
      enableMoveables
    } else if (e.peer.getButton == MouseEvent.BUTTON3) {
      enableHint
    }
  }

  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    val tiles = game.sortedTiles
    var hint:TilePair = if (showHint) game.hint else null
    for (tile <- tiles) {
      if (hint != null)
        drawTile(g, tile, (hint.tile1 == tile || hint.tile2 == tile))
      else
        drawTile(g, tile, false)
    }
  }

  def drawTile(g:Graphics2D, tile:Tile, isHint:Boolean) {
    if (!images.contains(tile.tileType.name))
      loadTileImage(tile.tileType.name)
    val x = tile.x * SwingFieldPanel.CellWidth - 5
    val y = tile.y * SwingFieldPanel.CellHeight - 5 - 5 * tile.z
    g.drawImage(images("tile"), x, y, null)
    g.drawImage(images(tile.tileType.name), x, y, null)
    if (showMoveable && !game.canMove(tile)) {
      g.drawImage(images("disabled"), x, y, null)
    } 
    if (isHint) {
      g.drawImage(images("hint"), x, y, null)
    }
    if (tile == game.selected) {
      g.drawImage(images("selected"), x, y, null)
    }
  }
}
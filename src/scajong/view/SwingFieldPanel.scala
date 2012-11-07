package scajong.view

import scajong.model._
import scajong.util._
import swing._
import swing.event._
import java.io.File
import java.awt.event.MouseEvent
import javax.imageio.ImageIO

object SwingFieldPanel {
  val CellWidth = 30
  val CellHeight = 20
  val TileImageWidth = 75
  val TileImageHeight= 95
}

class TileClickedEvent(val tile:Tile) extends Event
class HintEvent extends Event
class MoveablesEvent extends Event

class SwingFieldPanel(val field:Field, name:String) extends Panel with SimpleSubscriber {
  private var images = Map[String, Image]()
  private var showHint = false
  private var showMoveable = false
  preferredSize = new Dimension(field.width * SwingFieldPanel.CellWidth, 
      field.height * SwingFieldPanel.CellHeight)
  loadImages
  listenTo(mouse.clicks)
  field.addSubscriber(this)

  reactions += {
    case e: MouseReleased => mouseReleasedHandler(e)
  }
  
  override def processNotifications(sn:SimpleNotification) {
    sn match {
      case n:TileClickedNotification => repaint
      case n:SelectedTileNotification => repaint
      case n:CreatedGameNotification => repaint
      case _ => // Nothing
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
    val tiles = field.getSortedTiles.reverse
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
      //publish(new MoveablesEvent)
      //showMoveable = !showMoveable
      println("button2: " + showMoveable)
      repaint
    } else if (e.peer.getButton == MouseEvent.BUTTON3) {
      //publish(new HintEvent)
      //showHint = !showHint
      println("button3: " + showHint)
      repaint
    }
  }
  
  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    val tiles = field.getSortedTiles
    var hint:TilePair = if (showHint) field.getHint else null
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
  	if (showMoveable && !field.canMove(tile)) {
  	  g.drawImage(images("disabled"), x, y, null)
  	} 
  	if (isHint) {
  	  g.drawImage(images("hint"), x, y, null)
  	}
  	if (tile == field.selected) {
  	  g.drawImage(images("selected"), x, y, null)
  	}
  }
}
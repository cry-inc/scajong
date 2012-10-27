package scajong.view

import scajong.model._
import swing._
import swing.event._
import java.io.File
import java.awt.event.MouseEvent
import javax.swing.JFrame._
import javax.imageio.ImageIO

class TileClickedEvent(val tile:Tile) extends Event
class HintEvent extends Event
class MovablesEvent extends Event

class SwingView(field:Field, name:String = "") extends Frame {  
  title = "ScaJong"
  if (name.length > 0)
    title += " " + name;
  var fieldPanel = new FieldPanel(field, name)
  contents = fieldPanel
  visible = true
  // TODO: Exit after closed ALL views
  //peer.setDefaultCloseOperation(EXIT_ON_CLOSE)
  //reactions += {
  //  case e: WindowClosing => None
  //}
}

object FieldPanel {
  val CellWidth = 30
  val CellHeight = 20
  val TileImageWidth = 75
  val TileImageHeight= 95
}

class FieldPanel(val field:Field, name:String) extends Panel {
  private var images = Map[String, Image]()
  private var showHint = false
  private var showMoveable = false
  preferredSize = new Dimension(Field.Width * FieldPanel.CellWidth, 
      Field.Height * FieldPanel.CellHeight)
  loadImages
  listenTo(mouse.clicks)
  listenTo(field)

  reactions += {
    case e: MouseReleased => mouseReleasedHandler(e)
    case e: FieldChangedEvent => repaint
  }

  def loadImages {
    for (tileType <- field.tileTypes)
      images += tileType.name -> ImageIO.read(new File("tiles/" + tileType.name + ".png"))
    val specials = Array("disabled", "selected", "empty", "tile", "hint")
    for (name <- specials)
    	images += name -> ImageIO.read(new File("tiles/" + name + ".png"))
  }
  
  def findTile(p:swing.Point) : Tile = {
    val tiles = field.getSortedTiles.reverse
    for (tile <- tiles) {
      val x = tile.x * FieldPanel.CellWidth - 5
      val y = tile.y * FieldPanel.CellHeight - 5 - 5 * tile.z
      val rect = new Rectangle(x, y, FieldPanel.TileImageWidth, FieldPanel.TileImageHeight)
      if (rect.contains(p)) {
        return tile
      }
    }  
    return null
  }
  
  def mouseReleasedHandler(e:event.MouseReleased) {
    if (e.peer.getButton == MouseEvent.BUTTON1) {
      val tile = findTile(e.point)
      println("button1: " + tile)
	    if (tile != null)
	      publish(new TileClickedEvent(tile))
	    println("button1: after publish")
    } else if (e.peer.getButton == MouseEvent.BUTTON2) {
      publish(new MovablesEvent)
      //showMoveable = !showMoveable
      println("button2: " + showMoveable)
      repaint
    } else if (e.peer.getButton == MouseEvent.BUTTON3) {
      publish(new HintEvent)
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
    println("repainted")
  }
  
  def drawTile(g:Graphics2D, tile:Tile, isHint:Boolean) {
    val x = tile.x * FieldPanel.CellWidth - 5
    val y = tile.y * FieldPanel.CellHeight - 5 - 5 * tile.z
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
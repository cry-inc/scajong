package scajong.view

import scajong.model._
import swing._
import swing.event._
import java.io.File
import javax.swing.JFrame._
import javax.imageio.ImageIO

class TileClickedEvent(val tile:Tile) extends Event

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
  
  var images : Map[String, Image] = Map()
  /*
  var hint1:Tile = null
  var hint2:Tile = null
  var showMoveable:Boolean = true
  */
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
  
  def drawTile(g:Graphics2D, tile:Tile) {
    val x = tile.x * FieldPanel.CellWidth - 5
    val y = tile.y * FieldPanel.CellHeight - 5 - 5 * tile.z
    
    g.drawImage(images("tile"), x, y, null)
    g.drawImage(images(tile.tileType.name), x, y, null)
    
    /*
  	if (showMoveable && !field.canMove(tile))
	    g.drawImage(images("disabled"), x, y, null)
  	
  	if (tile == hint1 || tile == hint2)
  	    g.drawImage(images("hint"), x, y, null)
    */
  	if (tile == field.selected)
  	  g.drawImage(images("selected"), x, y, null)
  }
  
  def mouseReleasedHandler(e:event.MouseReleased) {
    // TODO: skip all but the left mouse button
    val tiles = field.getSortedTiles.reverse
    for (tile <- tiles) {
      val x = tile.x * FieldPanel.CellWidth - 5
      val y = tile.y * FieldPanel.CellHeight - 5 - 5 * tile.z
      val rect = new Rectangle(x, y, FieldPanel.TileImageWidth, FieldPanel.TileImageHeight)
      if (rect.contains(e.point)) {
        publish(new TileClickedEvent(tile))
        return
      }
    }
  }
  
  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    val tiles = field.getSortedTiles
    for (tile <- tiles)
      drawTile(g, tile)
  }
}
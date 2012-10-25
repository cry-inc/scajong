package de.htwg.scajong.view.swing

import swing._
import swing.event.Event
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame._
import de.htwg.scajong.model._
import scala.swing.event.MouseClicked

class TileClickedEvent(val tile:Tile) extends Event

class SwingView(field:Field, name:String) extends Frame {  
  title = "ScaJong"
  var fieldPanel = new FieldPanel(field, name)
  peer.setDefaultCloseOperation(EXIT_ON_CLOSE)
  contents = fieldPanel
  visible = true
}

object FieldPanel {
  val CellWidth = 30
  val CellHeight = 20
  val TileImageWidth = 75
  val TileImageHeight= 95
}

class FieldPanel(val field:Field, name:String) extends Panel {
  
  var images : Map[String, Image] = Map()
  
  preferredSize = new Dimension(Field.Width * FieldPanel.CellWidth, 
      Field.Height * FieldPanel.CellHeight)
  loadImages
  listenTo(mouse.clicks)

  reactions += {
    case e: MouseClicked => mouseClickHandler(e)
    // TODO: update inactive view/window
    // TODO: remove field var from view, add to events
    // TODO: add view to view events
    case e: Event => {println("repaint " + name); repaint; }
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
	if (_showMoveable && !_field.CanMove(tile))
	    g.DrawImage(_disImage, rect);
	
	if (_showHint && (tile == _hint1 || tile == _hint2))
	    g.DrawImage(_hintImage, rect);
	*/
    
	if (tile == field.selected)
	  g.drawImage(images("selected"), x, y, null)
  }
  
  def mouseClickHandler(e:event.MouseClicked) : Boolean = {
    val tiles = field.getSortedTiles.reverse
    for (tile <- tiles) {
      val x = tile.x * FieldPanel.CellWidth - 5
      val y = tile.y * FieldPanel.CellHeight - 5 - 5 * tile.z
      val rect = new Rectangle(x,y, FieldPanel.TileImageWidth, FieldPanel.TileImageHeight)
      if (rect.contains(e.point)) {
        publish(new TileClickedEvent(tile))
        return true
      }
    }
    return false
  }
  
  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    val tiles = field.getSortedTiles
    for (tile <- tiles)
      drawTile(g, tile)
  }
}
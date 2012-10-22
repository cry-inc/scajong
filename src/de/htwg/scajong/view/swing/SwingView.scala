package de.htwg.scajong.view.swing

import swing._
import java.io.File                                                           
import javax.imageio.ImageIO

import de.htwg.scajong.model._

class SwingView(field:Field) extends Frame {  
  title = "ScaJong"
  contents = new FieldPanel(field)
  visible = true
}

object FieldPanel {
  val CellWidth = 30
  val CellHeight = 20
}

class FieldPanel(val field:Field) extends Panel {
  
  var images : Map[String, Image] = Map()
  
  preferredSize = new Dimension(Field.Width * FieldPanel.CellWidth, 
      Field.Height * FieldPanel.CellHeight)
  
  //println(new File(".").getCanonicalPath())
  //var image = ImageIO.read(new File("test.png"))
  
  loadImages

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
	
	if (tile == _selected)
	    g.DrawImage(_selImage, rect);
	*/
  }
  
  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    //g.setColor(new Color(0, 0, 0))
    val tiles = field.getSortedTiles
    for (tile <- tiles)
      drawTile(g, tile)
  }
}
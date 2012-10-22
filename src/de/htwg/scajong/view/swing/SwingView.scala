package de.htwg.scajong.view.swing

import swing._
import java.io.File                                                           
import javax.imageio.ImageIO

import de.htwg.scajong.model._

object SwingView {
  def main(args: Array[String]) {
    new SwingView()
  }
}

class SwingView extends Frame {
  /*
  val panel = new FlowPanel()
  val button = new Button("Click me")
  val label = new Label()

  panel.contents += button
  panel.contents += label
  */
  
  title = "ScaJong"
  contents = new FieldPanel(null) //panel
  visible = true
}

object FieldPanel {
  val CellWidth = 30
  val CellHeight = 20
}

class FieldPanel(val field:Field) extends Panel {
  
  var images : Array[Image] = Array()
  
  preferredSize = new Dimension(Field.Width * FieldPanel.CellWidth, 
      Field.Height * FieldPanel.CellHeight)
  
  //println(new File(".").getCanonicalPath())
  //var image = ImageIO.read(new File("test.png"))
  
  //loadImages

  def loadImages {
    images = new Array(field.tileTypes.length)
    var i = 0
    for (tileType <- field.tileTypes) {
      images(i) = ImageIO.read(new File(tileType.name + ".png"))
      i += 1
    }
  }
  
  override def paintComponent(g: Graphics2D) : Unit = {
    g.setColor(new Color(255, 255, 255))
    g.fillRect(0, 0, preferredSize.width, preferredSize.height)
    
    g.setColor(new Color(0, 0, 0))
    g.drawString("BLAH", 20, 20)
    g.drawRect(200, 200, 200, 200)
    
    //g.drawImage(image, 0, 0, null)
  }
}
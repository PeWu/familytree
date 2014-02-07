package familytree.graphics

import java.awt.Graphics2D

trait ImageTree extends PositionedImage {
  def drawNode(graphics: Graphics2D)

  def draw(graphics: Graphics2D) {
    drawNode(graphics)
    childNodes.foreach(_.drawAtPosition(graphics))
  }

  def childNodes: Seq[PositionedImage]
}

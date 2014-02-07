package familytree.graphics

import familytree.util.{Point, Rectangle}
import java.awt.Graphics2D

// Draws the given image with a margin on all sides.
case class Margin(image: Image, margin: Int) extends Image {

  def boundingBox = new Rectangle(image.left, image.top, image.width + 2 * margin, image.height + 2 * margin)

  def draw(graphics: Graphics2D) {
    image.drawAt(graphics, Point(margin, margin))
  }
}

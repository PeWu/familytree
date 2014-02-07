package familytree.graphics

import java.awt.Graphics2D
import familytree.util.{Point, Positioned, Bounded}

trait Drawable {
  def draw(graphics: Graphics2D)

  def drawAt(graphics: Graphics2D, position: Point) {
    val transform = graphics.getTransform
    graphics.translate(position.x, position.y)
    draw(graphics)
    graphics.setTransform(transform)
  }
}

trait Image extends Drawable with Bounded

trait PositionedImage extends Image with Positioned {
  def drawAtPosition(graphics: Graphics2D) = drawAt(graphics, position)
}

object PositionedImage {
  def apply(image: Image, imagePosition: Point = Point(0, 0)) = new PositionedImage {
    override lazy val position: Point = imagePosition
    override def draw(graphics: Graphics2D) = image.draw(graphics)
    override lazy val boundingBox = image.boundingBox
  }
}

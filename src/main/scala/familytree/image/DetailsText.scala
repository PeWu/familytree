package familytree.image

import java.awt.{Font, Graphics2D}
import familytree.util.Rectangle
import familytree.graphics.Image

// Draws a list of strings, optionally with a symbol to the left of the list in the first line.
case class DetailsText(details: List[String], symbol: Option[String]) extends Image {
  val detailsFont = new Font("verdana", Font.PLAIN, 10)
  val startX = if (symbol.isDefined) 13 else 0

  override def draw(graphics: Graphics2D) {
    if (details.nonEmpty) {
      graphics.setFont(detailsFont)
      for (s <- symbol)
        ImageUtil.centerString(graphics, s, 7, 8)
      for ((s, i) <- details.zipWithIndex)
        graphics.drawString(s, startX,  8 + 10 * i)
    }
  }

  override lazy val boundingBox = {
    val width = (0 ::details.map(s => startX + ImageUtil.textWidth(s, detailsFont))).max
    val height = 10 * details.size
    new Rectangle(0, 0, width, height)
  }
}

package familytree.image

import java.awt.{Font, Graphics2D}
import java.awt.font.FontRenderContext

object ImageUtil {
  def centerString(graphics: Graphics2D, text: String, x: Int, y: Int) {
    val width = textWidth(graphics, text, graphics.getFont)
    graphics.drawString(text, x - width / 2, y)
  }

  def textWidth(graphics: Graphics2D, text: String, font: Font): Int =
    textWidth(text, font, graphics.getFontRenderContext)

  val fontRenderContext = new FontRenderContext(null, false, false)
  def textWidth(text: String, font: Font): Int =
    textWidth(text, font, fontRenderContext)

  def textWidth(text: String, font: Font, fontContext: FontRenderContext): Int =
    font.getStringBounds(text, fontContext).getWidth.toInt
}

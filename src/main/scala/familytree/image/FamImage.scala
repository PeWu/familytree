package image

import gedcom.Fam
import util.{Rectangle, Bounded}
import graphics.{Image, Drawable}
import java.awt.{Font, Color, Graphics2D}
import java.awt.geom.RoundRectangle2D

// Draws a box corresponding to a family.
case class FamImage(family: Fam) extends Image {
  override lazy val boundingBox = new Rectangle(0, 0, 100, 20)

  val detailsFont = new Font("verdana", Font.PLAIN, 10)

  override def draw(graphics: Graphics2D) {
    val box = new RoundRectangle2D.Double(0, 0, boundingBox.width, boundingBox.height, 15, 15)
    graphics.setColor(Color.WHITE)
    graphics.fill(box)
    graphics.setColor(Color.BLACK)
    val oldClip = graphics.getClip
    graphics.clip(box)

    graphics.setFont(detailsFont)
    family.marriageDate.foreach { d =>
      ImageUtil.centerString(graphics, "oo", 13, 12)
      graphics.drawString(d, 25,  12)
    }

    // Finish
    graphics.setClip(oldClip)
    graphics.draw(box)
  }
}

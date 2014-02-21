package familytree.image

import familytree.gedcom.Fam
import familytree.util.{Point, Rectangle}
import familytree.graphics.Image
import java.awt.{Font, Color, Graphics2D}
import java.awt.geom.RoundRectangle2D

case class FamImageConfig(
  showPlaces: Boolean = true,
  showDates: Boolean = true)

// Draws a box corresponding to a family.
case class FamImage(family: Fam, config: FamImageConfig) extends Image {
  val minHeight = 20
  val minWidth = 100

  override lazy val boundingBox = {
    val width = math.max(minWidth, details.width)
    val height = minHeight + math.max(0, details.height - 10)
    new Rectangle(0, 0, width, height)
  }

  // Date and place of marriage
  val marriageDetails = List(
    if (config.showDates) family.marriageDate else None,
    if (config.showPlaces) family.marriagePlace else None)
    .flatten

  lazy val details = DetailsText(marriageDetails, Some("oo"))

  override def draw(graphics: Graphics2D) {
    val box = new RoundRectangle2D.Double(0, 0, boundingBox.width, boundingBox.height, 5, 5)
    graphics.setColor(Color.WHITE)
    graphics.fill(box)
    graphics.setColor(Color.BLACK)
    val oldClip = graphics.getClip
    graphics.clip(box)

    details.drawAt(graphics, Point(0, 4))

    // Finish
    graphics.setClip(oldClip)
    graphics.draw(box)
  }
}

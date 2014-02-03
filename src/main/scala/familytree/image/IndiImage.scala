package image

import graphics.{Image, Drawable}
import java.awt.{Color, Font, Graphics2D}
import gedcom.{Female, Male, Sex, Indi}
import java.awt.font.FontRenderContext
import java.awt.geom.RoundRectangle2D
import util.{Rectangle, Bounded}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.FileInputStream

// Draws a box corresponding to a person.
case class IndiImage(indi: Indi, generation: Int) extends Image {
  val textMargin = 5
  val minWidth = 110
  val defaultHeight = 64

  val nameFont = new Font("verdana", Font.BOLD, 12)
  val detailsFont = new Font("verdana", Font.PLAIN, 10)

  lazy val image = indi.image.map { imagePath =>
      ImageIO.read(new FileInputStream(indi.image.get))
  }

  override val height = defaultHeight

  lazy val (imageWidth, imageHeight) =
    image.map { image =>
      (image.getWidth * height / image.getHeight, height)
    }.getOrElse((0, 0))

  override lazy val boundingBox = {
    val w1 = ImageUtil.textWidth(indi.firstName, nameFont) + 2 * textMargin
    val w2 = ImageUtil.textWidth(indi.lastName, nameFont) + 2 * textMargin
    new Rectangle(0, 0, math.max(math.max(minWidth, w1), w2) + imageWidth, height)
  }

  def draw(graphics: Graphics2D) {
    val box = new RoundRectangle2D.Double(0, 0, boundingBox.width, boundingBox.height, 15, 15)
    graphics.setColor(boxColor)
    graphics.fill(box)
    graphics.setColor(Color.BLACK)
    val oldClip = graphics.getClip
    graphics.clip(box)

    graphics.setFont(nameFont)
    val midX = (width - imageWidth) / 2
    ImageUtil.centerString(graphics, indi.firstName, midX, 14)
    ImageUtil.centerString(graphics, indi.lastName, midX, 26)

    graphics.setFont(detailsFont)
    indi.birthDate.foreach { d =>
      ImageUtil.centerString(graphics, "*", 7, 38)
      graphics.drawString(d, 13,  38)
    }
    indi.deathDate.foreach { d =>
      ImageUtil.centerString(graphics, "+", 7, 48)
      graphics.drawString(d, 13,  48)
    }
    indi.sex.foreach { sex =>
      graphics.setFont(sexSymbolFont)
      graphics.drawString(sexSymbol(sex), width - imageWidth - 14, height - 4)
    }
    for( i <- image) {
      graphics.drawImage(i, width - imageWidth, 0, imageWidth, imageHeight, null)
    }

    // Finish
    graphics.setClip(oldClip)
    graphics.draw(box)
  }

  // Different colors for different generations.
  private val boxColors = Array(
    new Color(0xff, 0xff, 0xff), // -13
    new Color(0xce, 0xb6, 0xbd), // -12
    new Color(0xde, 0x55, 0xff), // -11
    new Color(0x84, 0x82, 0xff), // -10
    new Color(0xad, 0xae, 0xef), // -9
    new Color(0xad, 0xcf, 0xff), // -8
    new Color(0xe7, 0xdb, 0xe7), // -7
    new Color(0xd6, 0x5d, 0x5a), // -6
    new Color(0xff, 0x82, 0xb5), // -5
    new Color(0xef, 0xae, 0xc6), // -4
    new Color(0xff, 0xdd, 0xdd), // -3
    new Color(0xce, 0xaa, 0x31), // -2
    new Color(0xff, 0xdd, 0x00), // -1

    new Color(0xff, 0xff, 0x33), // 0

    new Color(0xff, 0xff, 0xdd), // 1
    new Color(0xde, 0xff, 0xde), // 2
    new Color(0x82, 0xff, 0x82), // 3
    new Color(0x1a, 0xe1, 0x1a), // 4
    new Color(0xa9, 0xd0, 0xa9), // 5
    new Color(0xa9, 0xd0, 0xbf), // 6
    new Color(0xbb, 0xbb, 0xbb), // 7
    new Color(0xaa, 0x95, 0x95), // 8
    new Color(0x9e, 0xa3, 0xb2), // 9
    new Color(0xcd, 0xd3, 0xe9), // 10
    new Color(0xdf, 0xe2, 0xe2), // 11
    new Color(0xfa, 0xfa, 0xfa), // 12
    new Color(0xff, 0xff, 0xff) // 13
  )

  def boxColor = {
    val mid = (boxColors.size - 1) / 2
    if (generation <= 0)
      boxColors(-((-generation - 1) % mid) + mid - 1)
    else
      boxColors((generation - 1) % mid + mid + 1)
  }

  def sexSymbol(sex: Sex) = sex match {
    case Male => "\u2642"
    case Female => "\u2640"
  }

  val sexSymbolFont = {
    val maleSymbol = sexSymbol(Male).charAt(0)
    val candidateFonts = List("sansserif", "apple symbol", "symbol").map(new Font(_, Font.PLAIN, 10))
    candidateFonts.find(_.canDisplay(maleSymbol)).getOrElse(candidateFonts.head)
  }

}

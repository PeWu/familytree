package familytree.image

import familytree.graphics.Image
import java.awt.{Color, Font, Graphics2D}
import familytree.gedcom.{Female, Male, Sex, Indi}
import java.awt.geom.RoundRectangle2D
import familytree.util.{Point, Rectangle}
import javax.imageio.ImageIO
import java.io.FileInputStream

case class IndiImageConfig(
  showId: Boolean = true,
  showSex: Boolean = true,
  showPlaces: Boolean = true,
  showDates: Boolean = true,
  showImages: Boolean = true,
  useColors: Boolean = true)

// Draws a box corresponding to a person.
case class IndiImage(indi: Indi, generation: Int, config: IndiImageConfig) extends Image {
  val textMargin = 5
  val minWidth = 110
  val defaultHeight = 64

  val nameFont = new Font("verdana", Font.BOLD, 12)
  val detailsFont = new Font("verdana", Font.PLAIN, 10)
  val idFont = new Font("verdana", Font.ITALIC, 10)

  lazy val image = indi.image.map { imagePath =>
      ImageIO.read(new FileInputStream(indi.image.get))
  }

  lazy val (imageWidth, imageHeight) =
    if (config.showImages)
      image.map { image =>
        (image.getWidth * defaultHeight / image.getHeight, defaultHeight)
      }.getOrElse((0, 0))
    else
      (0, 0)

  override lazy val boundingBox = {
    val w1 = ImageUtil.textWidth(indi.firstName, nameFont) + 2 * textMargin
    val w2 = ImageUtil.textWidth(indi.lastName, nameFont) + 2 * textMargin
    val dataWidth = (w1 :: w2 :: minWidth :: details.map(_.width)).max
    val dataHeight = details.map(_.height).sum
    val height = defaultHeight + math.max(0, dataHeight - 20)
    new Rectangle(0, 0, dataWidth + imageWidth, height)
  }

  // Date and place of birth
  lazy val birthDetails = new Image {
    val details = List(
      if (config.showDates) indi.birthDate else None,
      if (config.showPlaces) indi.birthPlace else None)
      .flatten

    override def draw(graphics: Graphics2D) {
      if (details.nonEmpty) {
        ImageUtil.centerString(graphics, "*", 7, 8)
        for ((s, i) <- details.zipWithIndex)
          graphics.drawString(s, 13,  8 + 10 * i)
      }
    }

    override lazy val boundingBox = {
      val width = (0 ::details.map(s => 13 + ImageUtil.textWidth(s, detailsFont))).max
      val height = 10 * details.size
      new Rectangle(0, 0, width, height)
    }
  }

  // Date and place of death
  lazy val deathDetails = new Image {
    val details = List(
      if (config.showDates) indi.deathDate else None,
      if (config.showPlaces) indi.deathPlace else None)
      .flatten

    override def draw(graphics: Graphics2D) {
      if (details.nonEmpty) {
        ImageUtil.centerString(graphics, "+", 7, 8)
        for ((s, i) <- details.zipWithIndex)
          graphics.drawString(s, 13,  8 + 10 * i)
      }
    }

    override lazy val boundingBox = {
      val width = (0 ::details.map(s => 13 + ImageUtil.textWidth(s, detailsFont))).max
      val height = 10 * details.size
      new Rectangle(0, 0, width, height)
    }
  }

  lazy val details: List[Image] =
    List(birthDetails, deathDetails)

  def draw(graphics: Graphics2D) {
    val box = new RoundRectangle2D.Double(0, 0, boundingBox.width, boundingBox.height, 15, 15)
    if (config.useColors) {
      graphics.setColor(boxColor)
      graphics.fill(box)
    }
    graphics.setColor(Color.BLACK)
    val oldClip = graphics.getClip
    graphics.clip(box)

    graphics.setFont(nameFont)
    val midX = (width - imageWidth) / 2
    ImageUtil.centerString(graphics, indi.firstName, midX, 14)
    ImageUtil.centerString(graphics, indi.lastName, midX, 26)

    graphics.setFont(detailsFont)
    details.foldLeft(30) { (y, image) =>
      image.drawAt(graphics, Point(0, y))
      y + image.height
    }
    if (config.showId) {
      graphics.setFont(idFont)
      graphics.drawString(indi.id, 8, height - 4)
    }
    if (config.showSex) {
      indi.sex.foreach { sex =>
        graphics.setFont(sexSymbolFont)
        graphics.drawString(sexSymbol(sex), width - imageWidth - 14, height - 4)
      }
    }
    if (config.showImages) {
      for( i <- image) {
        graphics.drawImage(i, width - imageWidth, 0, imageWidth, imageHeight, null)
      }
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

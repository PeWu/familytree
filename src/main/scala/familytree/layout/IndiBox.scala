package familytree.layout

import familytree.gedcom.{Fam, Indi}
import familytree.util.{Rectangle, Point}
import familytree.graphics.{ImageTree, PositionedImage}
import java.awt.Graphics2D
import familytree.image.{FamImage, IndiImage}

// Represents the box corresponding to a person.
// position: position of top left corner of the IndiBox relative to the previous IndiBox top left corner
case class IndiBox(
    individual: Indi,
    family: Option[Fam],
    links: IndiBoxLinks,
    generation: Int = 0,
    position: Point = Point(0, 0)) extends ImageTree {
  override lazy val boundingBox: Rectangle = {
    val linkedObjectBounds = links.all.map(x => x.boundingBox + x.position)
    val allBounds = image.boundingBox :: famImage.map(i => i.boundingBox + i.position).toList ::: linkedObjectBounds
    allBounds.reduce(_.union(_))
  }

  override def drawNode(graphics: Graphics2D) {
    drawLines(graphics)
  }

  override lazy val childNodes = PositionedImage(image) :: famImage.toList ::: links.all

  def drawLines(graphics: Graphics2D) {
    if (children.nonEmpty) {
      val xs = children.map(c => c.x + c.image.width / 2)
      val y = children.head.y
      val midX = spouse match {
        case Some(s) => (image.right + s.image.right) / 2
        case _ => image.right / 2
      }
      val midY = y - Layout.verticalGap / 2
      xs.foreach(x => graphics.drawLine(x, y, x, midY))
      val parentY =
        if (famImage.isDefined)
          famImage.get.height + famImage.get.y
        else
          image.bottom - 10
      graphics.drawLine(midX, parentY, midX, midY)
      val allXs = (midX :: xs).toSet
      if (allXs.size > 1)
        graphics.drawLine(allXs.min, midY, allXs.max, midY)
    }
    if (parent.isDefined) {
      val x = image.right / 2
      val midY = parent.get.y + parent.get.famImage.get.y + parent.get.famImage.get.bottom + Layout.verticalGap / 2
      graphics.drawLine(x, 0, x, midY)
      if (parent.get.children.size > 0) {
        val siblingXs = parent.get.children.map(c => c.x + c.image.width / 2)
        val siblingX = if (parent.get.x > 0)
          siblingXs.min
        else
          siblingXs.max
        val parentX = parent.get.x + siblingX
        graphics.drawLine(x, midY, parentX, midY)
      } else {
        val parentX =
          if (parent.get.spouse.isDefined)
            parent.get.x + parent.get.image.width
          else
            parent.get.x + parent.get.image.width / 2
        val parentY =
          if (parent.get.famImage.isDefined)
            parent.get.bottom
          else
            parent.get.bottom - 10
        graphics.drawLine(parentX, midY, parentX, parent.get.y + parentY)
        if (x != parentX)
          graphics.drawLine(x, midY, parentX, midY)
      }
    }
  }

  lazy val image = IndiImage(individual, generation)

  lazy val famImage = family.map { family =>
    val famImage = FamImage(family)
    val w = spouse.map(_.image.width).getOrElse(0)
    val famX = (image.width + w - famImage.width) / 2
    val position = Point(famX, image.height)
    PositionedImage(famImage, position)
  }

  def parent = links.parent
  def spouse = links.spouse
  def nextMarriage = links.nextMarriage
  def children = links.children
}

case class IndiBoxLinks(
    spouse: Option[IndiBox],
    parent: Option[IndiBox],
    nextMarriage: Option[IndiBox],
    children: List[IndiBox]) {
  lazy val all = List(spouse, parent, nextMarriage).flatten ++ children
}

package familytree.layout

import familytree.util.{Rectangle, Point}

trait ParentDirection
case object ParentLeft extends ParentDirection
case object ParentTop extends ParentDirection
case object ParentRight extends ParentDirection

// Lays out the tree so that nothing collides
object CenteredLayout {
  def doLayout(indibox: IndiBox): IndiBox = {
    val links = layoutLinks(indibox)
    indibox.copy(
      links=links,
      position=Point(0, 0),
      minSize=Point(0, spouseHeight(links.spouse)))
  }

  def layoutLinks(indibox: IndiBox): IndiBoxLinks = {
    val spouse = indibox.spouse.map(layoutSpouse(indibox, _))
    val parent = indibox.parent.map(layoutParent(indibox, _))
    val nextMarriage = indibox.nextMarriage.map(layoutNextMarriage(indibox, _))

    val newIndibox = indibox.copy(minSize = Point(0, spouseHeight(spouse)))
    val children = layoutChildren(newIndibox, newIndibox.children)
    IndiBoxLinks(spouse, parent, nextMarriage, children)
  }

  def layoutSpouse(prev: IndiBox, spouse: IndiBox) = {
    val links = layoutLinks(spouse)
    val links2 =
      if (prev.parent.isDefined && links.parent.isDefined) {
        val x1 = -links.parent.get.left + Layout.horizontalGap / 2
        val x2 = -(0 :: links.parent.get.children.map(c => c.left + c.x)).min + Layout.horizontalGap / 2 + spouse.image.width / 2
        val x = math.max(x1, x2)
        val y = -links.parent.get.bottom - Layout.verticalGap
        val spouseParentPosition = Point(x, y)
        val spouseParent = links.parent.get.copy(position=spouseParentPosition)
        spouse.links.copy(parent=Some(spouseParent))
      } else links
    spouse.copy(links=links2, position=Point(prev.image.width, 0), minSize=Point(0, prev.image.height))
  }

  def layoutParent(prev: IndiBox, parent: IndiBox) = {
    val links = layoutLinks(parent)
    val layedOutParent: IndiBox = parent.copy(links=links, minSize=Point(0, spouseHeight(links.spouse)))
    val position = positionParent(prev, layedOutParent)
    layedOutParent.copy(position=position)
  }

  def positionParent(prev: IndiBox, parent: IndiBox) = {
    val parentY = -parent.bottom - Layout.verticalGap
    val parentX =
      if (prev.spouse.isDefined && prev.spouse.get.parent.isDefined) {
        val x1 = prev.image.right - parent.right - Layout.horizontalGap / 2
        val x2 = -(0 :: parent.children.map(c => c.right + c.x)).max + prev.image.width / 2 - Layout.horizontalGap / 2
        math.min(x1, x2)
      } else {
        prev.image.right / 2 - parent.image.width
      }
    Point(parentX, parentY)
  }

  def layoutNextMarriage(prev: IndiBox, nextMarriage: IndiBox) = {
    nextMarriage
  }

  def layoutChildren(prev: IndiBox, children: List[IndiBox]) = {
    val layedOut = children.map(doLayout)
    val boundingBoxes = layedOut.map(_.boundingBox)
    val totalWidth = boundingBoxes.map(_.width + Layout.horizontalGap).sum - Layout.horizontalGap
    val parentWidth = prev.image.right + prev.spouse.map(_.image.right).getOrElse(0)
    val offset = (totalWidth - parentWidth) / 2
    val offsets = childrenOffsets(boundingBoxes)
    layedOut.zip(offsets).map(c => c._1.copy(position=Point(c._2 - offset, prev.bottom + Layout.verticalGap)))
  }

  def childrenOffsets(bounds: List[Rectangle], accum: Int = 0): List[Int] = {
    bounds match {
      case Nil => Nil
      case head :: tail =>
        val offset = accum - head.left
        offset :: childrenOffsets(tail, offset + head.right + Layout.horizontalGap)
    }
  }

  private def spouseHeight(spouse: Option[IndiBox]) = spouse.map(_.image.height).getOrElse(0)
}

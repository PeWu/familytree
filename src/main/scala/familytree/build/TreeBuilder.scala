package familytree.build

import familytree.gedcom.Indi
import familytree.layout.{IndiBoxLinks, IndiBox}

object TreeBuilder {
  // The number of ancestors of the first individual to include in the tree.
  // TODO: Make this
  val maxAncestors = 100

  // Builds the tree starting from the given individual.
  def build(indi: Indi): IndiBox = {
    val family = indi.familiesWhereSpouse.headOption
    val spouse = buildSpouse(indi, 0)
    val parent = buildParent(indi, 1)
    val nextMarriage = buildNextMarriage(indi)
    val children = buildChildren(indi, 0, 1)
    val links = IndiBoxLinks(spouse, parent, nextMarriage, children)
    IndiBox(indi, family, links)
  }

  private def buildSpouse(indi: Indi, genUp: Int): Option[IndiBox] = {
    indi.spouse.map { spouse =>
      val parent = buildParent(spouse, genUp + 1)
      val links = IndiBoxLinks(None, parent, None, Nil)
      IndiBox(spouse, None, links, genUp)
    }
  }

  private def buildOnlySpouse(indi: Indi, generation: Int): Option[IndiBox] = {
    indi.spouse.map { spouse =>
      val links = IndiBoxLinks(None, None, None, Nil)
      IndiBox(spouse, None, links, generation)
    }
  }

  private def buildParent(indi: Indi, genUp: Int): Option[IndiBox] = {
    if (genUp > maxAncestors)
      None
    else
      indi.father.orElse(indi.mother).map { parentIndi =>
        val family = indi.familyWhereChild
        val spouse = buildSpouse(parentIndi, genUp)
        val parent = buildParent(parentIndi, genUp + 1)
        val nextMarriage = buildNextMarriage(parentIndi)
        val children = buildChildren(parentIndi, genUp, 1, omit=Some(indi))
        val links = IndiBoxLinks(spouse, parent, nextMarriage, children)
        IndiBox(parentIndi, family, links, genUp)
      }
  }

  private def buildNextMarriage(indi: Indi): Option[IndiBox] = None

  private def buildChildren(indi: Indi, genUp: Int, genDown: Int, omit: Option[Indi]=None) = {
    val children = indi.children.filter(i => !omit.isDefined || i.id != omit.get.id)
    children.map(buildChild(_, genUp, genDown))
  }

  private def buildChild(indi: Indi, genUp: Int, genDown: Int): IndiBox = {
    val family = indi.familiesWhereSpouse.headOption
    val children = buildChildren(indi, genUp, genDown + 1)
    val spouse = buildOnlySpouse(indi, genUp - genDown)
    val links = IndiBoxLinks(spouse, None, None, children)
    IndiBox(indi, family, links, genUp - genDown)
  }
}

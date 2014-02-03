package gedcom

// Finds the best individual in the gedcom file to start drawing the tree. This is the person for whom the tree will
// contain the largest.
class BestIndiFinder(gedcom: Gedcom) {
  private var upMap = Map[String, Int]()
  private var downMap = Map[String, Int]()

  def find: Indi = {
    val i = gedcom.indis.values.maxBy(graphSize)
    def goUp(i: Indi): Indi =
      if (i.spouse.isDefined)
        i
      else
        i.father.orElse(i.mother).map(goUp).getOrElse(i)
    goUp(i)
  }

  private def graphSize(i: Indi): Int = {
    up(i) + down(i)
  }

  private def up(i: Indi): Int = {
    upMap.get(i.id).getOrElse {
      val parents = List(i.father, i.mother).flatten
      val x = parents.map(up).sum + parents.size
      val y = math.max(0, (0 :: parents.map(down)).max - (down(i) + 1))
      val v = x + y
      upMap += i.id -> v
      v
    }
  }

  private def down(i: Indi): Int = {
    downMap.get(i.id).getOrElse {
      val v = i.children.map(down).sum + i.children.size
      downMap += i.id -> v
      v
    }
  }

  private def ancestorCount(i: Indi): Int = {
    val parents = List(i.father, i.mother).flatten
    parents.map(ancestorCount).sum + parents.size
  }

  private def descendentCount(i: Indi): Int = {
    i.children.map(descendentCount).sum + i.children.size
  }
}

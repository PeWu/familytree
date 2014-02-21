package familytree.gedcom

// An interface to the GEDCOM file.
trait Gedcom {
  def indis: Map[String, Indi]
}

sealed abstract class Sex
case object Male extends Sex
case object Female extends Sex

trait Indi {
  val id: String
  val firstName: String
  val lastName: String
  val spouse: Option[Indi]
  val father: Option[Indi]
  val mother: Option[Indi]
  val children: List[Indi]
  val familiesWhereSpouse: List[Fam]
  val familyWhereChild: Option[Fam]
  val birthDate: Option[String]
  val birthPlace: Option[String]
  val deathDate: Option[String]
  val deathPlace: Option[String]
  val image: Option[String]
  val sex: Option[Sex]
}

trait Fam {
  val marriageDate: Option[String]
  val marriagePlace: Option[String]
}

package familytree.gedcom

import scala.collection.JavaConverters._
import org.gedcom4j.model.{FamilyEventType, IndividualEventType}
import org.gedcom4j.parser.GedcomParser
import java.io._
import scala.Some

// Implementation of the Gedcom interface based on the gedcom4j library.
class G4jGedcom(val gedcom: org.gedcom4j.model.Gedcom, path: String) extends Gedcom {
  lazy val indis =
    gedcom.individuals
    .asScala
    .map(e => (e._1.replaceAll("@", ""), new G4jIndi(this, e._2)))
    .toMap

  val basePath = new File(path).getParent
}

class G4jIndi(baseGedcom: G4jGedcom, indi: org.gedcom4j.model.Individual) extends Indi {
  val gedcom = baseGedcom.gedcom

  override lazy val id = indi.xref.replaceAll("@", "")
  lazy val names = {
    val Names = """([^/]*)/?([^/]*)/?.*?""".r
    val name = indi.names.asScala.headOption.map(x => Option(x.basic)).flatten.headOption
    name match {
      case Some(Names(first, last)) => (first.trim, last.trim)
      case None => ("", "")
    }
  }
  override lazy val firstName = names._1
  override lazy val lastName = names._2

  override lazy val spouse = {
    if (indi.familiesWhereSpouse.isEmpty) {
      None
    } else {
      val family = Option(indi.familiesWhereSpouse.get(0).family)
      val spouse =
        family
          .map(f => if (f.husband == indi) Option(f.wife) else Option(f.husband))
          .flatten
          .map(_.xref)
          .map(gedcom.individuals.get)
      spouse.map(new G4jIndi(baseGedcom, _)).headOption
    }
  }

  override lazy val father: Option[Indi] = {
    if (indi.familiesWhereChild.isEmpty) {
      None
    } else {
      val father = Option(indi.familiesWhereChild.get(0).family.husband)
      father.map(new G4jIndi(baseGedcom, _))
    }
  }

  override lazy val mother: Option[Indi] = {
    if (indi.familiesWhereChild.isEmpty) {
      None
    } else {
      val mother = Option(indi.familiesWhereChild.get(0).family.wife)
      mother.map(new G4jIndi(baseGedcom, _))
    }
  }

  override lazy val children: List[Indi] = {
    indi.familiesWhereSpouse.asScala
      .flatMap(f => f.family.children.asScala)
      .map(i => gedcom.individuals.get(i.xref))
      .map(new G4jIndi(baseGedcom, _))
      .toList
  }

  override lazy val familiesWhereSpouse = {
    indi.familiesWhereSpouse.asScala.map(f => new G4jFam(f.family)).toList
  }

  override lazy val familyWhereChild = {
    if (indi.familiesWhereChild.isEmpty)
      None
    else
      Some(new G4jFam(indi.familiesWhereChild.get(0).family))
  }

  override val birthDate = {
    indi.getEventsOfType(IndividualEventType.BIRTH)
      .asScala
      .map(x => Option(x.date.value))
      .flatten
      .headOption
  }

  override val birthPlace = {
    indi.getEventsOfType(IndividualEventType.BIRTH)
      .asScala
      .map(x => Option(x.place).map(p => Option(p.placeName)))
      .flatten
      .flatten
      .headOption
  }

  override val deathDate = {
    indi.getEventsOfType(IndividualEventType.DEATH)
      .asScala
      .map(x => Option(x.date).map(d => Option(d.value)))
      .flatten
      .flatten
      .headOption
  }

  override val deathPlace = {
    indi.getEventsOfType(IndividualEventType.DEATH)
      .asScala
      .map(x => Option(x.place).map(p => Option(p.placeName)))
      .flatten
      .flatten
      .headOption
  }

  override val image = {
    for (
      media <- indi.multimedia.asScala.headOption;
      fileReference <- media.fileReferences.asScala.headOption;
      filename <- Option(fileReference.referenceToFile.value)
    ) yield baseGedcom.basePath + "/" + filename
  }

  override val sex = {
    Option(indi.sex).map(_.toString) match {
      case Some("F") => Some(Female)
      case Some("M") => Some(Male)
      case _ => None
    }
  }

  override lazy val toString = "Indi(" + firstName + " " + lastName + ")"
}

class G4jFam(fam: org.gedcom4j.model.Family) extends Fam {
  override val marriageDate = {
    fam.events.asScala.find(_.`type` == FamilyEventType.MARRIAGE)
      .map(x => Option(x.date).map(d => Option(d.value)))
      .flatten
      .flatten
      .headOption
  }
}

object G4jGedcom {
  def load(path: String): Gedcom = {
    val parser = new GedcomParser
    parser.load(path)
    new G4jGedcom(parser.gedcom, path)
  }

  def load(input: InputStream): Gedcom = {
    val parser = new GedcomParser
    parser.load(new BufferedInputStream(input))
    new G4jGedcom(parser.gedcom, "")
  }
}

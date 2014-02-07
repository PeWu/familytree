package familytree

import build.TreeBuilder
import gedcom.{BestIndiFinder, G4jGedcom, Gedcom}
import graphics.{Margin, PdfWriter}
import layout.CenteredLayout
import java.io.{OutputStream, FileOutputStream}

object Cli extends App {
  if (args.size < 2) {
    println("Too few arguments.")
    sys.exit(1)
  }
  val inputFile = args(0)
  val outputFile = args(1)

  val out = new FileOutputStream(outputFile)
  val gedcom = G4jGedcom.load(inputFile)
  generateTree(gedcom, out)
  out.close()

  def generateTree(gedcom: Gedcom, output: OutputStream) {
    val indi = new BestIndiFinder(gedcom).find
    val indibox = TreeBuilder.build(indi)
    val arranged = CenteredLayout.doLayout(indibox)
    PdfWriter.write(Margin(arranged, 20), output)
  }
}

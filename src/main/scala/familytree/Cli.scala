package familytree

import _root_.build.TreeBuilder
import _root_.gedcom.{BestIndiFinder, G4jGedcom, Gedcom}
import _root_.graphics.{Margin, PdfWriter}
import _root_.layout.CenteredLayout
import java.io.{OutputStream, FileOutputStream}

object Cli extends App {
  if (args.size < 2) {
    println("Too few arguments.")
    sys.exit(1)
  }
  val inputFile = args(0)
  val outputFile = args(1)

  val out = new FileOutputStream(outputFile)
  val gedcom: Gedcom = G4jGedcom.load(inputFile)
  generateTree(gedcom, out)
  out.close()

  def generateTree(gedcom: Gedcom, output: OutputStream) {
    val indi = new BestIndiFinder(gedcom).find
    val indibox = TreeBuilder.build(indi)
    val arranged = CenteredLayout.doLayout(indibox)
    PdfWriter.write(Margin(arranged, 20), output)
  }
}

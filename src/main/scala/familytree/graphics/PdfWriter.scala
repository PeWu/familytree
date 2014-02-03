package graphics

import org.apache.fop.svg.PDFDocumentGraphics2D
import java.io.{OutputStream, FileOutputStream}
import org.apache.xmlgraphics.java2d.GraphicContext
import util.Bounded

// Writes the given image to a PDF file.
object PdfWriter extends FileWriter {
  def write(image: Image, outputStream: OutputStream) {
    val pdfGraphics = new PDFDocumentGraphics2D(true, outputStream, image.boundingBox.width, image.boundingBox.height)
    pdfGraphics.setGraphicContext(new GraphicContext)
    pdfGraphics.drawRect(-1, -1, 1, 1)  // Initialize context. Otherwise first translate doesn't work.
    val transform = pdfGraphics.getTransform
    pdfGraphics.translate(-image.boundingBox.left, -image.boundingBox.top)
    image.draw(pdfGraphics)
    pdfGraphics.setTransform(transform)
    pdfGraphics.finish()
  }
}

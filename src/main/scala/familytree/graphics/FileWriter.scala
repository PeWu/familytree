package graphics

import java.io.{FileOutputStream, OutputStream}

trait FileWriter {
  def write(image: Image, outputStream: OutputStream)

  def write(image: Image, filename: String) {
    println("Writing to " + filename)
    val out = new FileOutputStream(filename)
    write(image, out)
    out.close()
  }
}

package familytree.util

trait Bounded {
  def boundingBox: Rectangle

  def width = boundingBox.width
  def height = boundingBox.height
  def left = boundingBox.left
  def right = boundingBox.right
  def top = boundingBox.top
  def bottom = boundingBox.bottom
}

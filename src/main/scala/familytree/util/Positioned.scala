package familytree.util

trait Positioned {
  val position: Point
  def x = position.x
  def y = position.y
}

package util

case class Rectangle(a: Point, b: Point) {
  def this(x: Int, y: Int, width: Int, height: Int) =
    this(Point(x, y), Point(x + width, y + height))

  def x = a.x
  def y = a.y
  def width = math.abs(a.x - b.x)
  def height = math.abs(a.y - b.y)
  def top = math.min(a.y, b.y)
  def bottom = math.max(a.y, b.y)
  def left = math.min(a.x, b.x)
  def right = math.max(a.x, b.x)
  def min = Point(left, top)
  def max = Point(right, bottom)
  def empty = a == b
  def +(p: Point) = Rectangle(a + p, b + p)

  def union(r: Rectangle) = {
    if (empty)
      r
    else if (r.empty)
      this
    else
      Rectangle(
        Point(math.min(left, r.left), math.min(top, r.top)),
        Point(math.max(right, r.right), math.max(bottom, r.bottom)))
  }
}

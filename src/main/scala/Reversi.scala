object Reversi{
  def main(args: Array[String]) : Unit = {
    println("Welcome to Reversi")
    println(mesh())
  }

  val eol: String = sys.props("line.separator")
  def bar(cellWidth:Int=3, cellNum:Int=8): String =
    ("+" + "-" * cellWidth) * cellNum + "+" + eol
  def cells(cellWidth:Int=3, cellNum:Int=8): String =
    ("|" + " " * cellWidth) * cellNum + "+" + eol
  def mesh(cellWidth:Int=3, cellNum:Int=8): String =
    (bar(cellWidth, cellNum) + cells(cellWidth, cellNum)) * cellNum + bar(cellWidth, cellNum)
}
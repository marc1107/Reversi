val eol = sys.props("line.separator")
def bar(cellWidth:Int=3, cellNum:Int=8) =
  ("+" + "-" * cellWidth) * cellNum + "+" + eol
def cells(cellWidth:Int=3, cellNum:Int=8) =
  ("|" + " " * cellWidth) * cellNum + "+" + eol
def mesh(cellWidth:Int=3, cellNum:Int=8) =
  (bar(cellWidth, cellNum) + cells(cellWidth, cellNum)) * cellNum + bar(cellWidth, cellNum)

println("Welcome to Reversi")
println(mesh())

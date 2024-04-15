package matrixComponent

import matrixComponent.Matrix
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class MatrixSpec extends AnyWordSpec {
  "A Matrix is a tailor-made immutable data type that contains a two-dimensional Vector of something. A Matrix" when {
    "empty " should {
      "be created by using a dimension and a sample cell" in {
        val matrix = new Matrix[String](2, "b")
        matrix.size should be(2)
      }
      "for test purposes only be created with a Vector of Vectors" in {
        val testMatrix = Matrix(Vector(Vector("b")))
        testMatrix.size should be(1)
      }

    }
    "filled" should {
      val matrix = new Matrix[String](2, "b")
      "give access to its cells" in {
        matrix.cell(0, 0) should be("b")
      }
      "replace cells and return a new data structure" in {
        val returnedMatrix = matrix.replaceCell(0, 0, "w")
        matrix.cell(0, 0) should be("b")
        returnedMatrix.cell(0, 0) should be("w")
      }
      "be filled using fill operation" in {
        val returnedMatrix = matrix.fill("b")
        returnedMatrix.cell(0, 0) should be("b")
      }
    }
  }

}

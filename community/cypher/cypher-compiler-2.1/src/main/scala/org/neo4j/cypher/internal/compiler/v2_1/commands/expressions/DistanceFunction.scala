package org.neo4j.cypher.internal.compiler.v2_1.commands.expressions

import org.neo4j.cypher.internal.compiler.v2_1._
import pipes.QueryState
import symbols._
import org.neo4j.graphdb.Node
import org.neo4j.cypher.CypherTypeException

/** DistanceFunction
 *  @param a Expression that evaluates to WKT String (ex: "POINT (1.0 2.0)") or Node with x,y properties
 *  @param b Expression that evaluates to WKT String or Node with x,y properties
 *  @return Euclidean distance between x and y
 */

case class DistanceFunction (a:Expression, b:Expression) extends Expression {

  /**
   *
   * @param a WKT String (currently only supports POINT(x y) form), or Node with x,y attributes
   * @return Tuple (x,y) points parsed from WKT String s
   */
  def getXYVals(a: Any): (Double, Double) = {
    val WKTPointPattern = """POINT ?\(([-]?\d+\.?\d+?) ([-]?\d+\.?\d+?)\)""".r

    a match {
      case s: String =>
        s match {
          case WKTPointPattern(x, y) =>
            (x.toDouble, y.toDouble) // FIXME: exception handling of some sort here?
          case _ => throw new CypherTypeException("String parameters to distance() should be of form " +
            "POINT(x y). Call to distance() with String parameter not of this form.")
        }
      case n: Node =>
        (n.getProperty("x").asInstanceOf[Double], n.getProperty("y").asInstanceOf[Double])
      case _ =>
        throw new CypherTypeException("distance() expected type Node, String, or a combination of Node" +
          "and String, but was called with some other type");
    }
  }

  def computeDistance(a:(Double, Double), b:(Double,Double)): Double = {
    math.sqrt( math.pow(b._1 - a._1, 2.0) + math.pow(b._2 - a._2, 2.0))
  }

  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {
    computeDistance(getXYVals(a(ctx)), getXYVals(b(ctx)))
  }

  def rewrite(f: (Expression) => Expression) = f(DistanceFunction(a.rewrite(f), b.rewrite(f)))

  def arguments = Seq(a, b)

  def symbolTableDependencies = a.symbolTableDependencies   // FIXME: add b symbolTableDependencies ( +)

  protected def calculateType(symbols: SymbolTable) = {     // FIXME: b.evaluteType ?
    a.evaluateType(CTNode, symbols)
    CTCollection(CTString)
  }


}

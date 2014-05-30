package org.neo4j.cypher.internal.compiler.v2_1.commands.expressions

import org.neo4j.cypher.internal.compiler.v2_1._
import pipes.QueryState
import symbols._
import org.neo4j.graphdb.Node
import org.neo4j.cypher.CypherTypeException

/** DistanceFunction
 *  @param x Expression that evaluates to WKT String (ex: "POINT (1.0 2.0)") or Node with x,y properties
 *  @param y Expression that evaluates to WKT String or Node with x,y properties
 *  @return Euclidean distance between x and y
 */

case class DistanceFunction (x:Expression, y:Expression) extends Expression {

  /**
   *
   * @param s WKT String (currently only supports POINT(x y) form)
   * @return Tuple (x,y) points parsed from WKT String s
   */
  def getXYVals(s: String): (Double, Double) = {
    val WKTPointPattern = """POINT ?\(([-]?\d+\.?\d+?) ([-]?\d+\.?\d+?)\)""".r

    s match {
      case WKTPointPattern(x, y) =>
        (x.toDouble, y.toDouble)  // FIXME: exception handling of some sort here?
      case _ => throw new CypherTypeException("String parameters to distance() should be of form " +
        "POINT(x y). Call to distance() with String parameter not of this form.")
    }
  }

  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = (x(ctx), y(ctx)) match {
    case (a: Node, b: Node) =>
      math.sqrt( math.pow(b.getProperty("x").asInstanceOf[Double] - a.getProperty("x").asInstanceOf[Double], 2.0) +
        math.pow(b.getProperty("y").asInstanceOf[Double]-a.getProperty("y").asInstanceOf[Double], 2.0))
    case (a: String, b: String) =>
      val (x1, y1) = getXYVals(a)
      val (x2, y2) = getXYVals(b)
      math.sqrt( math.pow(x2 - x1, 2.0) + math.pow(y2-y1, 2.0))
    case (a: Node, b: String) =>
      val (x2, y2) = getXYVals(b)
      math.sqrt(math.pow(x2 - a.getProperty("x").asInstanceOf[Double], 2.0) + math.pow(y2 - a.getProperty("y").asInstanceOf[Double], 2.0))
    case (a: String, b: Node) =>
      val (x1, y1) = getXYVals(a)
      math.sqrt(math.pow(b.getProperty("x").asInstanceOf[Double] - x1, 2.0) + math.pow(b.getProperty("y").asInstanceOf[Double] - y1, 2.0))
    case (_, null) =>
      null
    case (null, _) =>
      null
    case (_, _) =>
      throw new CypherTypeException("distance() expected type Node, String, or a combination of Node" +
        "and String, but was called with some other type");

  }

  def rewrite(f: (Expression) => Expression) = f(DistanceFunction(x.rewrite(f), y.rewrite(f)))

  def arguments = Seq(x, y)

  def symbolTableDependencies = x.symbolTableDependencies   // FIXME: add y symbolTableDependencies ( +)

  protected def calculateType(symbols: SymbolTable) = {     // FIXME: y.evaluteType ?
    x.evaluateType(CTNode, symbols)
    CTCollection(CTString)
  }


}

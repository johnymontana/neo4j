package org.neo4j.cypher.internal.compiler.v2_1.commands.expressions

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.neo4j.collections.rtree.SpatialIndexReader
import org.neo4j.cypher.internal.compiler.v2_1._
import org.neo4j.cypher.internal.spi.v2_1.SpatialTransactionBoundQueryContext
import org.neo4j.gis.spatial.Layer
import pipes.QueryState
import symbols._
import org.neo4j.graphdb.Node
import org.neo4j.cypher.CypherTypeException

/** DistanceFunction
 *  @param a Expression that evaluates to WKT String (ex: "POINT (1.0 2.0)") or Node with x,y properties
 *  @param b Expression that evaluates to WKT String or Node with x,y properties
 *  @param layerExp Expression that evaluates to String name of spatial layer
 *  @return Euclidean distance between x and y
 */

case class DistanceFunction (a:Expression, b:Expression, layerExp:Expression) extends Expression {

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

  // TODO: This should be made as abstract as possible for resuse in a util object at some point
  def getGeometry(a: Any, layer: Layer): Geometry = {
    a match {
      case s: String =>
        // Assume WKT, figure out error handling here
        val reader: WKTReader = new WKTReader()
        reader.read(s)
      case n: Node =>
        layer.getGeometryEncoder.decodeGeometry(n)
    }
  }



  def computeDistance(a:(Double, Double), b:(Double,Double)): Double = {
    math.sqrt( math.pow(b._1 - a._1, 2.0) + math.pow(b._2 - a._2, 2.0))
  }

  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {
    //computeDistance(getXYVals(a(ctx)), getXYVals(b(ctx)))
    val layer: Layer = state.query.asInstanceOf[SpatialTransactionBoundQueryContext].getLayer(layerExp(ctx).asInstanceOf[String])
    //val spatialIndex: SpatialIndexReader = layer.getIndex

    getGeometry(a(ctx), layer).distance(getGeometry(b(ctx), layer))
  }

  def rewrite(f: (Expression) => Expression) = f(DistanceFunction(a.rewrite(f), b.rewrite(f), layerExp.rewrite(f)))

  def arguments = Seq(a, b, layerExp)

  def symbolTableDependencies = a.symbolTableDependencies   // FIXME: add b symbolTableDependencies ( +)

  protected def calculateType(symbols: SymbolTable) = {     // FIXME: b.evaluteType ?
    a.evaluateType(CTNode, symbols)
    CTCollection(CTString)
  }


}

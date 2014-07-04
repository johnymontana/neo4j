package org.neo4j.cypher.internal.compiler.v2_1.spatial

/**
 * Created by lyonwj on 6/17/14.
 */
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.neo4j.collections.rtree.SpatialIndexReader
import org.neo4j.collections.rtree.filter.SearchResults
import org.neo4j.cypher.CypherTypeException
import org.neo4j.cypher.internal.spi.v2_1.SpatialTransactionBoundQueryContext
import org.neo4j.gis.spatial._
import org.neo4j.cypher.internal.compiler.v2_1.ExecutionContext
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.Expression
import org.neo4j.cypher.internal.compiler.v2_1.pipes.QueryState
import org.neo4j.cypher.internal.compiler.v2_1.symbols._
import org.neo4j.gis.spatial.filter.{SearchRecords, SearchIntersect}
import org.neo4j.graphdb.Node

import scala.collection.JavaConverters._


/**
 * Created by lyonwj on 6/17/14.
 * Initial proof of concept for creating Spatial Layers,
 * NOTE: this should not be a function, instead refactor this logic into a Pipe(?)
 *
 */


/**
 *
 * @param a Should evaluate to String (layer name / label)
 * @param b Should evaluate to String (layer type, will make use of RegisteredLayerType)
 */
case class SpatialCreateLayerFunction(a:Expression, b:Expression) extends Expression {


  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {

    def ensureEvalString(exp: Expression): String = {
      exp(ctx) match {
        case s: String => s
        case _ => throw new CypherTypeException("CREATE SPATIAL LAYER must be called with String arguments")

      }
    }

    val layerName: String = ensureEvalString(a)
    val layerType: String = ensureEvalString(b)

    state.query.asInstanceOf[SpatialTransactionBoundQueryContext].createLayer(layerName, layerType)
    layerName

  }

  def arguments = Seq(a, b)

  def rewrite(f: (Expression) => Expression) = f(SpatialCreateLayerFunction(a.rewrite(f), b.rewrite(f)))

  def symbolTableDependencies = a.symbolTableDependencies   // FIXME: add b symbolTableDependencies ( +)

  protected def calculateType(symbols: SymbolTable) = {     // FIXME: b.evaluteType ?
    a.evaluateType(CTNode, symbols)
    CTCollection(CTString)
  }
}

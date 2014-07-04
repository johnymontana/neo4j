package org.neo4j.cypher.internal.compiler.v2_1.spatial


/**
 * Created by lyonwj on 6/27/14.
 */


import org.neo4j.cypher.CypherTypeException
import org.neo4j.cypher.internal.compiler.v2_1.ExecutionContext
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.Expression
import org.neo4j.cypher.internal.compiler.v2_1.pipes.QueryState
import org.neo4j.cypher.internal.compiler.v2_1.symbols.SymbolTable
import org.neo4j.cypher.internal.compiler.v2_1.symbols._
import org.neo4j.cypher.internal.spi.v2_1.SpatialTransactionBoundQueryContext
import org.neo4j.gis.spatial.Layer
import org.neo4j.graphdb.Node

/**
 *
 * @param a Expression that evaluates to Node
 * @param b Expression that evaluates to String, the name of the layer
 */
case class SpatialAddNodeFunction(a: Expression, b:Expression) extends Expression {
  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {
    def ensureEvalString(exp: Expression): String = {
      exp(ctx) match {
        case s: String => s
        case _ => throw new CypherTypeException("CREATE SPATIAL LAYER must be called with String arguments")

      }
    }

    def ensureEvalNode(exp: Expression): Node = {
      exp(ctx) match {
        case n: Node => n
        case _ => throw new CypherTypeException("SpatialAddNode expected a Node but found a different type")
      }
    }

    val node: Node = ensureEvalNode(a)
    val layerName: String = ensureEvalString(b)
    val layer: Layer = state.query.asInstanceOf[SpatialTransactionBoundQueryContext].getLayer(layerName)

    layer.add(node)
    node

  }

  def arguments = Seq(a, b)

  def rewrite(f: (Expression) => Expression) = f(SpatialAddNodeFunction(a.rewrite(f), b.rewrite(f)))

  def symbolTableDependencies = a.symbolTableDependencies

  protected def calculateType(symbols: SymbolTable) = {
    a.evaluateType(CTNode, symbols)
    //CTCollection(CTString)
    CTNode

  }



}

/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    val layer: Layer = state.query.spatialOps.getLayer(layerName).getOrElse(null)
    if (layer == null) {
      throw new CypherTypeException("SpatialAddNode did not find the specified Layer")
    } else {
      layer.add(node)
    }
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

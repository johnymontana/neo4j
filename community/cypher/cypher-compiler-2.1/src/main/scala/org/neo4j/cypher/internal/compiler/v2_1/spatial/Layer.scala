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

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

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.neo4j.collections.rtree.SpatialIndexReader
import org.neo4j.collections.rtree.filter.SearchResults

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
 *
 * Take Nodes or strings (WKT)
 * Convert from Node to Geometry using GeometryEncoder for the
 * specific layer
 * Conversion from string assuming WKT
 * Call spatial intersects
 */


case class IntersectsFunction(a:Expression, b:Expression, layerExp:Expression) extends Expression {

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

  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {
    // Assume we have a PointLayer 'Geometry"
    // FIXME: where to get actual layer name
    //val layer: Layer = state.query.asInstanceOf[SpatialTransactionBoundQueryContext].getLayer(layerExp(ctx).asInstanceOf[String])
    val layer: Layer = state.query.spatialOps.getLayer(layerExp(ctx).asInstanceOf[String])

    val spatialIndex: SpatialIndexReader = layer.getIndex

    // FIXME: inefficient (BAD) implementation here, refactor this:
    // Get geometry for Expression b
    // Find all Nodes in layer that intersect b
    // Iterate results and compare Node ids, if a == b then return true, else false
    // FIXME: this means Expression a must evaluate to a Node, and is not consistent with the intent of this function
    val nodeA: Node = a(ctx).asInstanceOf[Node]
    val searchQuery: SearchIntersect = new SearchIntersect(layer, getGeometry(b(ctx), layer))
    val results: SearchResults = spatialIndex.searchIndex(searchQuery)

    var intersectResult = false
    for (r <- results.iterator().asScala) {
      if (r.getId() == nodeA.getId()){
        intersectResult = true
      }
    }
    println(intersectResult)
    intersectResult

  }

  def arguments = Seq(a, b, layerExp)

  def rewrite(f: (Expression) => Expression) = f(IntersectsFunction(a.rewrite(f), b.rewrite(f), layerExp.rewrite(f)))

  def symbolTableDependencies = a.symbolTableDependencies   // FIXME: add b symbolTableDependencies ( +)

  protected def calculateType(symbols: SymbolTable) = {     // FIXME: b.evaluteType ?
    a.evaluateType(CTNode, symbols)
    CTCollection(CTString)
  }
}

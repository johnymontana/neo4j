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
package org.neo4j.cypher.internal.compiler.v2_1.commands.expressions

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.neo4j.gis.spatial.encoders.SimplePointEncoder
import org.neo4j.gis.spatial.rtree.SpatialIndexReader
import org.neo4j.cypher.internal.compiler.v2_1._
import org.neo4j.gis.spatial.{GeometryEncoder, Layer}
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

case class DistanceFunction (a:Expression, b:Expression, layerExp:Expression = new Null) extends Expression {

  /**
   * Convert either a string as WKT, or a Node as a Spatial Geometry node to a JTS Geometry object for further processing
   *
   * @param a: Any - String or Node
   * @param layer: Layer - Required for Node objects to get the decoder
   * @return Geometry (JTS)
   *
   * TODO: Craig: Should move into QueryContext, so compiler has no dependencies on JTS or Spatial
   */
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

  def getNode(a: Any): Node = {
    a match {
      case n: Node => n
      case _ => null
    }
  }

  override def apply(ctx: ExecutionContext)(implicit state: QueryState): Any = {
    //computeDistance(getXYVals(a(ctx)), getXYVals(b(ctx)))
    val name: String = layerExp(ctx).asInstanceOf[String]
    val layer: Layer = state.query.getLayer(name, getNode(a))
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

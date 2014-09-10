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
package org.neo4j.cypher.internal.compiler.v2_1.commands

import org.mockito.Mockito
import org.mockito.Mockito._
import org.neo4j.cypher.internal.commons.CypherFunSuite
import org.neo4j.cypher.internal.compiler.v2_1.ExecutionContext
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.{Literal, DistanceFunction, Identifier}
import org.neo4j.cypher.internal.compiler.v2_1.pipes.{QueryState, QueryStateHelper}
import org.neo4j.cypher.internal.compiler.v2_1.spatial.IntersectsFunction
import org.neo4j.cypher.internal.compiler.v2_1.spi.QueryContext
import org.neo4j.gis.spatial.Layer
import org.neo4j.gis.spatial.encoders.SimplePointEncoder
import org.neo4j.graphdb.Node

/**
 * Created by lyonwj on 6/18/14.
 */
class IntersectsTest extends CypherFunSuite {

  test("givenTwoNodesLogIntersectSearchResults") {
    val node1 = mockedNodeWithXY(1, 15.34, 20.0)
    val node2 = mockedNodeWithXY(2, 15.34, 20.0)
    val state = mockedQueryStateWithSimplePointLayer("layer", "x:y", Set(node1, node2))
    val ctx = ExecutionContext() += ("n1" -> node1) += ("n2" -> node2)

    val result = IntersectsFunction(Identifier("n1"), Identifier("n2"), Literal("layer"))(ctx)(state)

    println(result)

    result.asInstanceOf[Boolean] should be (true)

    //result.asInstanceOf[boolean] should be true
  }

  /**
   * Configure the mocks to behave like the real simple point layer
   */
  private def mockedQueryStateWithSimplePointLayer(name: String, config: String, nodes: Set[Node]): QueryState = {
    val queryContext = mock[QueryContext]
    val state = QueryStateHelper.emptyWith(query = queryContext)
    val layer: Layer = mock[Layer]
    val geometryEncoder = new SimplePointEncoder
    geometryEncoder.setConfiguration(config)
    when(queryContext.getLayer(name,null)).thenReturn(layer)
    for(node <- nodes) {
      when(queryContext.getLayer(name,node)).thenReturn(layer)
    }
    when(layer.getGeometryEncoder).thenReturn(geometryEncoder)
    state
  }

  /** Mock Node with x, y attributes
    *
    * @param id
    * @param x
    * @param y
    * @return
    */
  private def mockedNodeWithXY(id: Long, x: Double, y: Double): Node = {
    val node = mock[Node]

    Mockito.when(node.getProperty("x").asInstanceOf[Double]).thenReturn(x)
    Mockito.when(node.getProperty("y").asInstanceOf[Double]).thenReturn(y)
    node
  }
}

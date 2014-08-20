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

import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions._
import org.junit.Test
import org.scalatest._
import Matchers._
import org.neo4j.cypher.internal.compiler.v2_1.ExecutionContext
import org.neo4j.cypher.internal.compiler.v2_1.spi.QueryContext
import org.neo4j.cypher.internal.compiler.v2_1.pipes.QueryStateHelper
import org.neo4j.graphdb.Node
import org.scalatest.mock.MockitoSugar
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.DistanceFunction
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.Literal
import org.mockito.Mockito


/** DistanceFunction tests
 *
 */
class DistanceTest extends Assertions with MockitoSugar {
  /**
   * Test WKT POINT(x y), POINT (x y)
   */
  @Test def givenAStringAndStringReturnDistance() {
    val func = new DistanceFunction(Literal("POINT (15.34 20)"), Literal("POINT (20 10)"), Literal("layer"))
    calc(func).asInstanceOf[Double] should be (11.03 +- 0.01)
  }

  /**
   * Test Node{x,y}, Node{x,y}
   */
  @Test def givenANodeAndNodeReturnDistance() {
    val node1 = mockedNodeWithXY(1, 15.34, 20.0)
    val node2 = mockedNodeWithXY(2, 20.0, 10.0)
    val queryContext = mock[QueryContext]

    val state = QueryStateHelper.emptyWith(query = queryContext)
    val ctx = ExecutionContext() += ("n1" -> node1) += ("n2" -> node2)
    val result = DistanceFunction(Identifier("n1"), Identifier("n2"), Literal("layer"))(ctx)(state)

    result.asInstanceOf[Double] should be (11.03 +- 0.01)

  }

  /**
   * Test Node{x,y}, WKT POINT(x y)
   */
  @Test def givenNodeStringReturnDistance() {
    val node = mockedNodeWithXY(1, 15.34, 20.0)
    val wkt_point = "POINT (20.0 10.0)"
    val queryContext = mock[QueryContext]
    val state = QueryStateHelper.emptyWith(query = queryContext)
    val ctx  = ExecutionContext() += ("n" -> node)

    val result = DistanceFunction(Identifier("n"), Literal(wkt_point), Literal("layer"))(ctx)(state)

    result.asInstanceOf[Double] should be (11.03 +- 0.01)
  }

  private def calc(e: Expression): Any = e(ExecutionContext.empty)(QueryStateHelper.empty)


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

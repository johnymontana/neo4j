package org.neo4j.cypher.internal.compiler.v2_1.spatial

import org.junit.Test
import org.neo4j.cypher.internal.compiler.v2_1.commands._
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions._
import org.mockito.Mockito
import org.neo4j.cypher.internal.compiler.v2_1.ExecutionContext
import org.neo4j.cypher.internal.compiler.v2_1.pipes.QueryStateHelper
import org.neo4j.cypher.internal.compiler.v2_1.spi.QueryContext
import org.neo4j.cypher.internal.spi.v2_1.SpatialTransactionBoundQueryContext
import org.neo4j.graphdb.Node
import org.scalatest.Assertions
import org.scalatest.mock.MockitoSugar
import org.scalatest.Matchers._

/**
 * Created by lyonwj on 6/18/14.
 */
class IntersectsTest extends Assertions with MockitoSugar {

  @Test def givenTwoNodesLogIntersectSearchResults() {
    val node1 = mockedNodeWithXY(1, 15.34, 20.0)
    val node2 = mockedNodeWithXY(2, 15.34, 20.0)
    val queryContext = mock[SpatialTransactionBoundQueryContext]

    val state = QueryStateHelper.emptyWith(query = queryContext)
    val ctx = ExecutionContext() += ("n1" -> node1) += ("n2" -> node2)

   // val result = IntersectsFunction(Identifier("n1"), Identifier("n2"))(ctx)(state)

   // println(result)

    //result should not be empty



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

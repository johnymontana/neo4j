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

/** Test Distance function
 *
 */
class DistanceTest extends Assertions with MockitoSugar {
  @Test def givenAStringAndStringReturnDistance() {
    val func = new DistanceFunction(Literal("POINT (15.34 20)"), Literal("POINT (20 10)"))
    calc(func).asInstanceOf[Double] should be (11.03 +- 0.01)
  }

  @Test def givenANodeAndNodeReturnDistance() {
    val node1 = mock[Node]
    val node2 = mock[Node]
    val queryContext = mock[QueryContext]

    node1.setProperty("x", 12.2)
    node1.setProperty("y", 10.2)

    node2.setProperty("x", 99.1)
    node2.setProperty("y", 33.4)


    val state = QueryStateHelper.emptyWith(query = queryContext)
    val ctx = ExecutionContext() += ("n1" -> node1) += ("n2" -> node2)

    // FIXME: how to mock nodes with properties? This doesn't seem to work
    val result = DistanceFunction(Identifier("n1"), Identifier("n2"))(ctx)(state)

    result.asInstanceOf[Double] should be (11.03 +- 0.01)
    // FIXME: this test fails, result evaluates to 0.0
  }

  private def calc(e: Expression): Any = e(ExecutionContext.empty)(QueryStateHelper.empty)
}

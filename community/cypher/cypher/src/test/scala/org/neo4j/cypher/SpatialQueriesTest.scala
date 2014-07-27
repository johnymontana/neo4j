package org.neo4j.cypher

/**
 * Created by lyonwj on 7/27/14.
 */
class SpatialQueriesTest extends ExecutionEngineFunSuite {

  test("test WKT RegisteredType layer with queries") {
    val result = engine.execute("CREATE (n1:WKTLayer {name: 'node1', geom: 'POINT(32.1 22.5)'})" +
      "WITH spatialCreateLayer('WKTLayer', 'WKT') AS layer, n1 " +
      "WITH spatialAddNode(n1, layer) AS node1, layer, n1 " +
      ""
      "RETURN n1.name as n");

    result.toList should equal(List(Map("n"->"node1")))
  }

}

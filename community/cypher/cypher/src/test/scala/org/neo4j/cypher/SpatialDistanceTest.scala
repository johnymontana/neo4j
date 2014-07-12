package org.neo4j.cypher

/**
 * Created by lyonwj on 7/11/14.
 */
class SpatialDistanceTest extends ExecutionEngineFunSuite {

  test("test distance with layer"){
    val result = engine.execute("CREATE (n1:SomeLayer {lat: 32.1, lon: 22.5, name: 'node1'}) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer, n3 " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer, n3 " +
      "MATCH (n:SomeLayer) WHERE Distance(n, n3, layer) < 10 RETURN n.name as n")
    //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2"), Map("n"->"node3")))
  }

}

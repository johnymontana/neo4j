package org.neo4j.cypher

/**
 * Created by lyonwj on 7/20/14.
 */
class SpatialIntersectsTest extends ExecutionEngineFunSuite {
  test("test Intersects with layer") {
    val result = engine.execute("CREATE (n1:SomeLayer {lat: 32.1, lon: 22.5, name: 'node1'}) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer, n1 " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer, n1, n2 " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer, n3, n1, n2 " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer, n3, n1, n2, n4 " +
      "MATCH (n:SomeLayer) WHERE Intersects(n, n2, layer) RETURN n.name as n")
    //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2"), Map("n"->"node3")))
  }

  test("test Intersects with layer and WKT string") {
    val result = engine.execute("CREATE (n1:SomeLayer {lat: 32.1, lon: 22.5, name: 'node1'}) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer, n3 " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer, n3 " +
      "MATCH (n:SomeLayer) WHERE Intersects(n, 'POINT(96.122 21.341)', layer) RETURN n.name as n")
    //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node4")))
  }

}

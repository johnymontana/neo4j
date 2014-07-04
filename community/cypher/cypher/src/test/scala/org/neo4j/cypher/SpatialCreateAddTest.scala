package org.neo4j.cypher

import org.neo4j.gis.spatial._
import org.neo4j.graphdb.Node

/**
 * Created by lyonwj on 6/11/14.
 */
class SpatialCreateAddTest extends ExecutionEngineFunSuite {


  test("test spatialCreateLayer func") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer return layer");
    result.toList should equal(List(Map("layer"->"SomeLayer")))
  }

  test("test spatialAddNode func") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'someNode'}) WITH spatialAddNode(n2, layer) as node " +
      "RETURN node.name as n")
    result.toList should equal(List(Map("n"->"someNode")))
  }

  test("test spatialAddNode func 2") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'someNode'}) WITH spatialAddNode(n2, layer) as node " +
      "RETURN node.name as n")
    result.toList should equal(List(Map("n"->"someNode")))
  }

  test("test create layer, add node and intersects with WKT") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer " +
      "MATCH (n:SomeLayer) WHERE Intersects(n, 'POINT(46.221 34.221)') RETURN n.name as n")
    //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2"), Map("n"->"node3")))
  }

  test("test create layer, add node and intersects with Node") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer, n3 " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer, n3 " +
      "MATCH (n:SomeLayer) WHERE Intersects(n, n3) RETURN n.name as n")
    //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2"), Map("n"->"node3")))
  }




}

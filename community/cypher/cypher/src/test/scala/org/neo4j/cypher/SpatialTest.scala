package org.neo4j.cypher

import org.neo4j.gis.spatial._
import org.neo4j.graphdb.Node

/**
 * Created by lyonwj on 6/11/14.
 */
class SpatialTest extends ExecutionEngineFunSuite {
  var spatialService:SpatialDatabaseService = null
  var geometryLayer: Layer = null

  var n1: Node = null
  var n2: Node = null
  var n3: Node = null
  var n4: Node = null

  override protected def initTest(){
    super.initTest()
    spatialService = new SpatialDatabaseService(graph)
    engine = new ExecutionEngine(graph, spatialService = Some(spatialService))
    geometryLayer = spatialService.getOrCreatePointLayer("SomeLayer", "x", "y")

    n1 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"one"), "Base")
    n2 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"two"), "SomeLayer")
    n3 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"three"), "SomeLayer")
    n4 = createLabeledNode(Map("x"->12.2, "y"->40.0, "name"->"four"), "SomeLayer")


    graph.inTx {
      geometryLayer.add(n1)// (PSQ, Pipe) -> (PSQ, Pipe)
      geometryLayer.add(n2)
      geometryLayer.add(n3)
      geometryLayer.add(n4)
    }

  }

  test("spatial hello world") {
//    val n1 = createLabeledNode(Map("x"->1.2, "y"->2.3), "Geometry")
//    val n2 = createLabeledNode(Map("x"->10.0, "y"->1000.0), "Geometry")
//    graph.inTx {
//      geometryLayer.add(n1)// (PSQ, Pipe) -> (PSQ, Pipe)
//      geometryLayer.add(n2)
//    }
    val result = engine.execute("MATCH (g:SomeLayer) WHERE Distance(g, 'POINT (1.0 2.0)') < 10 RETURN g")
    //println( execute("MATCH (g:Geometry) RETURN Distance(g, 'POINT (1.0 2.0)')").dumpToString())
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
    println(result.executionPlanDescription())

    //graph.
  }

  test("intersects test log") {


    val result = engine.execute("MATCH (b:Base), (g:SomeLayer) WHERE Intersects(g, b) RETURN g")

    println(result.executionPlanDescription())


    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))

    for (n <- result) {
      println(n.get("g"))
    }
  }

  test("test POINT WKT with intersects") {

    val result = engine.execute("MATCH (g:SomeLayer) WHERE Intersects(g, 'POINT(1.2 2.3)') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

  test("test POLYGON WKT with intersects") { // POINT(12.2 40) is not within this polygon
    val result = engine.execute("MATCH (g:SomeLayer) WHERE Intersects(g, 'POLYGON((0 0, 40 40, 20 40, 10 20, 0 0))') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

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

  test("test create layer, add node and intersects") {
    val result = engine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
      "CREATE (n2:SomeLayer {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayer {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer " +
      "CREATE (n4:SomeLayer {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer " +
      "MATCH (n:SomeLayer) WHERE Intersects(n, 'POINT(46.221 34.221)') RETURN n.name as n")
      //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2")))
  }




}

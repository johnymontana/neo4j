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
    geometryLayer = spatialService.getOrCreatePointLayer("Geometry", "x", "y")

    n1 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"one"), "Base")
    n2 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"two"), "Geometry")
    n3 = createLabeledNode(Map("x"->1.2, "y"->2.3, "name"->"three"), "Geometry")
    n4 = createLabeledNode(Map("x"->12.2, "y"->40.0, "name"->"four"), "Geometry")


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
    val result = engine.execute("MATCH (g:Geometry) WHERE Distance(g, 'POINT (1.0 2.0)') < 10 RETURN g")
    //println( execute("MATCH (g:Geometry) RETURN Distance(g, 'POINT (1.0 2.0)')").dumpToString())
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
    println(result.executionPlanDescription())
  }

  test("intersects test log") {


    val result = engine.execute("MATCH (b:Base), (g:Geometry) WHERE Intersects(g, b) RETURN g")

    println(result.executionPlanDescription())


    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))

    for (n <- result) {
      println(n.get("g"))
    }
  }

  test("test POINT WKT with intersects") {

    val result = engine.execute("MATCH (g:Geometry) WHERE Intersects(g, 'POINT(1.2 2.3)') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

  test("test POLYGON WKT with intersects") { // POINT(12.2 40) is not within this polygon
    val result = engine.execute("MATCH (g:Geometry) WHERE Intersects(g, 'POLYGON((0 0, 40 40, 20 40, 10 20, 0 0))') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

}

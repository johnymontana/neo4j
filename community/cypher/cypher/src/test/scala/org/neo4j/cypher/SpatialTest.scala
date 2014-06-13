package org.neo4j.cypher

import org.neo4j.gis.spatial._

/**
 * Created by lyonwj on 6/11/14.
 */
class SpatialTest extends ExecutionEngineFunSuite {
  var spatialService:SpatialDatabaseService = null
  var geometryLayer: Layer = null

  override protected def initTest(){
    super.initTest()
    spatialService = new SpatialDatabaseService(graph)
    engine = new ExecutionEngine(graph, spatialService = Some(spatialService))
    geometryLayer = spatialService.getOrCreatePointLayer("Geometry", "x", "y")

  }

  test("spatial hello world") {
    val n1 = createLabeledNode(Map("x"->1.2, "y"->2.3), "Geometry")
    val n2 = createLabeledNode(Map("x"->10.0, "y"->1000.0), "Geometry")
    graph.inTx {
      geometryLayer.add(n1)// (PSQ, Pipe) -> (PSQ, Pipe)
      geometryLayer.add(n2)
    }
    val result = execute("MATCH (g:Geometry) WHERE Distance(g, 'POINT (1.0 2.0)') < 10 RETURN g")
    //println( execute("MATCH (g:Geometry) RETURN Distance(g, 'POINT (1.0 2.0)')").dumpToString())
    result.toList should equal(List(Map("g"->n1)))
    println(result.executionPlanDescription())
  }

  //
}

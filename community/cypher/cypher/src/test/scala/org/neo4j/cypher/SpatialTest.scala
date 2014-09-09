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
    //Since spatialService is not used in the engine, we don't need to remake it here (I think William was planning to use it, but did not)
    //eengine = new ExecutionEngine(graph, spatialService = Some(spatialService))
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
    val result = eengine.execute("MATCH (g:SomeLayer) WHERE Distance(g, 'POINT (1.0 2.0)') < 10 RETURN g")
    //println( execute("MATCH (g:Geometry) RETURN Distance(g, 'POINT (1.0 2.0)')").dumpToString())
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
    println(result.executionPlanDescription())

    //graph.
  }

  test("intersects test log") {


    val result = eengine.execute("MATCH (b:Base), (g:SomeLayer) WHERE Intersects(g, b) RETURN g")

    println(result.executionPlanDescription())


    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))

    for (n <- result) {
      println(n.get("g"))
    }
  }

  test("test POINT WKT with intersects") {

    val result = eengine.execute("MATCH (g:SomeLayer) WHERE Intersects(g, 'POINT(1.2 2.3)') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

  test("test POLYGON WKT with intersects") { // POINT(12.2 40) is not within this polygon
    val result = eengine.execute("MATCH (g:SomeLayer) WHERE Intersects(g, 'POLYGON((0 0, 40 40, 20 40, 10 20, 0 0))') RETURN g")
    result.toList should equal(List(Map("g"->n2), Map("g"->n3)))
  }

  test("test spatialCreateLayer func") {
    val result = eengine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer return layer");
    result.toList should equal(List(Map("layer"->"SomeLayer")))
  }

  test("test spatialAddNode func") {
    val result = eengine.execute("CREATE (n1:SomeLayer) WITH spatialCreateLayer('SomeLayer','SimplePoint') as layer " +
                                "CREATE (n2:SomeLayer {y: 34.221, x: 46.221, name: 'someNode'}) WITH spatialAddNode(n2, layer) as node " +
                                "RETURN node.name as n")
    result.toList should equal(List(Map("n"->"someNode")))
  }

  test("test spatialAddNode func 2") {
    val result = eengine.execute("CREATE (n1:SomeLayerX) WITH spatialCreateLayer('SomeLayerX','SimplePoint') as layer " +
      "CREATE (n2:SomeLayerX {lat: 34.221, lon: 46.221, name: 'someNode'}) WITH spatialAddNode(n2, layer) as node " +
      "RETURN node.name as n")
    result.toList should equal(List(Map("n"->"someNode")))
  }

  test("test create layer, add node and intersects") {
    val result = eengine.execute("CREATE (n1:SomeLayerX {lat: 18.341, lon: 101.122, name: 'node1'}) " +
      "WITH n1, spatialCreateLayer('SomeLayerX','SimplePoint') as layer WITH spatialAddNode(n1,layer) as node1, layer " +
      "CREATE (n2:SomeLayerX {lat: 34.221, lon: 46.221, name: 'node2'}) WITH spatialAddNode(n2, layer) as node2, layer " +
      "CREATE (n3:SomeLayerX {lat: 34.221, lon: 46.221, name: 'node3'}) WITH spatialAddNode(n3, layer) as node3, layer " +
      "CREATE (n4:SomeLayerX {lat: 21.341, lon: 96.122, name: 'node4'}) WITH spatialAddNode(n4, layer) as node4, layer " +
      "MATCH (n:SomeLayerX) WHERE Intersects(n, 'POINT(46.221 34.221)') RETURN n.name as n")
      //"RETURN node.name as n")
    result.toList should equal(List(Map("n"->"node2"), Map("n"->"node3")))
  }




}

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

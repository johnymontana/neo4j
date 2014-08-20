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
 * Created by lyonwj on 7/27/14.
 */
class SpatialQueriesTest extends ExecutionEngineFunSuite {

  test("test WKT RegisteredType layer with queries") {
    val result = engine.execute("CREATE (n1:WKTLayer {name: 'node1', geom: 'POINT(32.1 22.5)'})" +
      "WITH spatialCreateLayer('WKTLayer', 'WKT') AS layer, n1 " +
      "WITH spatialAddNode(n1, layer) AS node1, layer, n1 " +
      "RETURN n1.name as n");

    result.toList should equal(List(Map("n"->"node1")))
  }

}

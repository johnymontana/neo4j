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
package org.neo4j.cypher.internal.compiler.v2_1.spatial


import org.scalatest.Matchers

import org.neo4j.graphdb.{Transaction, GraphDatabaseService, Node}
import java.util.List
import org.neo4j.gis.spatial._
import org.neo4j.graphdb.factory.GraphDatabaseFactory

import com.vividsolutions.jts.geom.{Envelope, Coordinate}
import org.neo4j.cypher.internal.commons.CypherFunSuite
import org.neo4j.gis.spatial.pipes.GeoPipeline


/** SpatialCypher tests
  * The purpose of these tests are to confirm Neo4j Spatial functionality is accessible from the Cypher project
  */
class SpatialCypherTest extends CypherFunSuite {

  var graphDb: GraphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("target/var/graph-db").newGraphDatabase
  var db: SpatialDatabaseService = new SpatialDatabaseService(graphDb)


  test("testBasicLayerOperations") {

    val layer: Layer = db.getOrCreateEditableLayer("test")
    layer should not be null

  }

  test("test simple point layer") {



    val layer: EditableLayer = db.createSimplePointLayer("testPointLayer", "Longitude", "Latitude").asInstanceOf[EditableLayer]
    layer should not be null //assertNotNull(layer)

    val record: SpatialRecord = layer.add(layer.getGeometryFactory.createPoint(new Coordinate(15.3, 56.2)))
    record should not be null //assertNotNull(record)

    val tx: Transaction = graphDb.beginTx()
    try {
      var results: List[SpatialDatabaseRecord] = GeoPipeline.startContainSearch(layer, layer.getGeometryFactory.toGeometry(new Envelope(15.0, 16.0, 56.0, 57.0))).toSpatialDatabaseRecordList


      results should have size 0
      results = GeoPipeline.startWithinSearch(layer, layer.getGeometryFactory.toGeometry(new Envelope(15.0, 16.0, 56.0, 57.0))).toSpatialDatabaseRecordList
      results should have size 1

    }
    tx.success()
  }



}


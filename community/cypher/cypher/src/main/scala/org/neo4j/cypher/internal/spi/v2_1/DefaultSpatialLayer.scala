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
package org.neo4j.cypher.internal.spi.v2_1

import com.vividsolutions.jts.geom.GeometryFactory
import org.neo4j.gis.spatial.attributes.PropertyMappingManager
import org.neo4j.gis.spatial.rtree.Listener
import org.neo4j.gis.spatial._
import org.neo4j.graphdb.Node
import org.opengis.referencing.crs.CoordinateReferenceSystem

object DefaultSpatialLayer extends Layer {
  override def initialize(p1: SpatialDatabaseService, p2: String, p3: Node): Unit = ???

  override def getDataset: SpatialDataset = ???

  override def getName: String = ???

  override def getIndex: LayerIndexReader = ???

  override def getGeometryType: Integer = ???

  override def getGeometryFactory: GeometryFactory = ???

  override def getGeometryEncoder: GeometryEncoder = ???

  override def getStyle: AnyRef = ???

  override def getLayerNode: Node = ???

  override def delete(p1: Listener): Unit = ???

  override def getPropertyMappingManager: PropertyMappingManager = ???

  override def getExtraPropertyNames: Array[String] = ???

  override def getSpatialDatabase: SpatialDatabaseService = ???

  override def getCoordinateReferenceSystem: CoordinateReferenceSystem = ???

  override def add(p1: Node): SpatialDatabaseRecord = ???
}



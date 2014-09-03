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
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.neo4j.gis.spatial.attributes.PropertyMappingManager
import org.neo4j.gis.spatial.encoders.SimplePointEncoder
import org.neo4j.gis.spatial.rtree.Listener
import org.neo4j.gis.spatial._
import org.neo4j.graphdb.Node
import org.opengis.referencing.crs.CoordinateReferenceSystem

class DefaultSpatialLayer(val spatial: SpatialDatabaseService) extends Layer {

  val name: String = "layer"
  val geometryFactory: GeometryFactory = new GeometryFactory
  val geometryEncoder: SimplePointEncoder = new SimplePointEncoder
  geometryEncoder.setConfiguration("x:y")

  override def initialize(service: SpatialDatabaseService, name: String, node: Node): Unit = {}

  override def getDataset: SpatialDataset = null

  override def getName: String = name

  override def getIndex: LayerIndexReader = null

  override def getGeometryType: Integer = Constants.GTYPE_POINT

  override def getGeometryFactory: GeometryFactory = geometryFactory

  override def getGeometryEncoder: GeometryEncoder = geometryEncoder

  override def getStyle: AnyRef = null

  override def getLayerNode: Node = null

  override def delete(listener: Listener) {}

  override def getPropertyMappingManager: PropertyMappingManager = null

  override def getExtraPropertyNames: Array[String] = Array[String]()

  override def getSpatialDatabase: SpatialDatabaseService = spatial

  override def getCoordinateReferenceSystem: CoordinateReferenceSystem = DefaultGeographicCRS.WGS84

  override def add(node: Node): SpatialDatabaseRecord = null

}



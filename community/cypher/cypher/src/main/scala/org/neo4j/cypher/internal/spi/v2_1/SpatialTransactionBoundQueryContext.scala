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

import org.neo4j.cypher.internal.compiler.v2_1.spi.{SpatialOperations, QueryContext}
import org.neo4j.gis.spatial.{Layer, SpatialDatabaseService}
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.kernel.api.Statement

/**
 * Created by lyonwj on 6/17/14.
 */
class SpatialTransactionBoundQueryContext(graph: GraphDatabaseAPI, var spatial:SpatialDatabaseService,
                                          tx: Transaction,
                                          isTopLevelTx: Boolean,
                                          statement: Statement) extends TransactionBoundQueryContext(graph, tx, isTopLevelTx, statement) with SpatialOperations{

  // FIXME: instantiate here or is this passed from somewhere (ExecutionEngine?)
  spatial = new SpatialDatabaseService(graph)

  override def withAnyOpenQueryContext[T](work: (QueryContext) => T): T = {
    if (open) {
      work(this)
    }
    else {
      val isTopLevelTx = !txBridge.hasTransaction
      val tx = graph.beginTx()
      try {
        val otherStatement = txBridge.instance()
        val result = try {
          work(new SpatialTransactionBoundQueryContext(graph, spatial, tx, isTopLevelTx, otherStatement))
        }
        finally {
          otherStatement.close()
        }
        tx.success()
        result
      }
      finally {
        tx.close()
      }
    }
  }

  //override var spatialOps: SpatialOperations = this

  // TODO: Spatial operations, these should probably be moved into a Trait SpatialOperations
  override def getLayer(name:String): Option[Layer] = {
    var layer: Layer = spatial.getLayer(name)
    if (layer == null) {
      return None
    } else {
      return Some(layer)
    }
  }

  override def createSimplePointLayer(name:String) = {
    //spatial.getOrCreatePointLayer(name, "x", "y") // FIXME: hardcoded x,y here
    spatial.getOrCreateRegisteredTypeLayer(name, "SimplePoint", null)
  }

  override def createLayer(name: String, layerType: String) = {
    spatial.getOrCreateRegisteredTypeLayer(name, layerType, null)
  }


}

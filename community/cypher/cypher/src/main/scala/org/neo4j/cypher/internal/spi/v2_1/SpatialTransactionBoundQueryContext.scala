package org.neo4j.cypher.internal.spi.v2_1

import org.neo4j.cypher.internal.compiler.v2_1.spi.QueryContext
import org.neo4j.gis.spatial.SpatialDatabaseService
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.kernel.api.Statement

/**
 * Created by lyonwj on 6/17/14.
 */
class SpatialTransactionBoundQueryContext(graph: GraphDatabaseAPI, var spatial:SpatialDatabaseService,
                                          tx: Transaction,
                                          isTopLevelTx: Boolean,
                                          statement: Statement) extends TransactionBoundQueryContext(graph, tx, isTopLevelTx, statement) {

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

  // Spatial operations, these should probably be moved into a Trait SpatialOperations
  def getLayer(name:String) = {
    spatial.getLayer(name)
  }

  def createSimplePointLayer(name:String) = {
    spatial.getOrCreatePointLayer(name, "x", "y") // FIXME: hardcoded x,y here
  }


}

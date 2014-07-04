package org.neo4j.cypher.internal.compiler.v2_1.spatial

import org.mockito.Mockito
import org.neo4j.cypher.internal.commons.CypherFunSuite
import org.neo4j.cypher.internal.spi.v2_1.{SpatialTransactionBoundQueryContext, TransactionBoundQueryContext}
import org.neo4j.gis.spatial.SpatialDatabaseService
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.api.Statement
import org.neo4j.kernel.impl.api.{KernelTransactionImplementation, KernelStatement}
import org.neo4j.test.ImpermanentGraphDatabase
import org.scalatest.{Assertions, FunSuite}
import org.scalatest.mock.MockitoSugar
import org.scalatest.Matchers._


/**
 * Created by lyonwj on 6/25/14.
 */
class SpatialTransactionBoundQueryContextTest extends CypherFunSuite with MockitoSugar with Assertions {
  var graph: ImpermanentGraphDatabase = null
  var outerTx: Transaction = null
  var statement: Statement = null
  var spatialService: SpatialDatabaseService = null
  var context: SpatialTransactionBoundQueryContext = null

  override protected def initTest(){
    graph = new ImpermanentGraphDatabase
    outerTx = mock[Transaction]
    statement = new KernelStatement(mock[KernelTransactionImplementation], null, null, null, null, null, null)
    spatialService = new SpatialDatabaseService(graph)
    context = new SpatialTransactionBoundQueryContext(graph, spatialService, outerTx, isTopLevelTx = true, statement)
  }

  test("init SpatialTransactionBoundQueryContent") {
//    graph = new ImpermanentGraphDatabase
//    outerTx = mock[Transaction]
//    statement = new KernelStatement(mock[KernelTransactionImplementation], null, null, null, null, null, null)
//    spatialService = new SpatialDatabaseService(graph)

    context.createSimplePointLayer("TestLayer")

    context.getLayer("TestLayer") should not be null


  }

  test("create SimplePoint layer") {
    context.createLayer("TestPointLayer", "SimplePoint")

    context.getLayer("TestPointLayer") should not be null
  }


  //Mockito.when(outerTx.failure()).thenThrow( new AssertionError( "Shouldn't be called" ) )
  //val context = new TransactionBoundQueryContext(graph, outerTx, isTopLevelTx = true, statement)




}

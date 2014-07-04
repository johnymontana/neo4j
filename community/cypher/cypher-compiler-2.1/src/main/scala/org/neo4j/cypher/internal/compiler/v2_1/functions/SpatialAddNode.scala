package org.neo4j.cypher.internal.compiler.v2_1.functions

import org.neo4j.cypher.internal.compiler.v2_1._
//import org.neo4j.cypher.internal.compiler.v2_1.functions.SpatialCreateLayer._
import org.neo4j.cypher.internal.compiler.v2_1.spatial.SpatialAddNodeFunction
import org.neo4j.cypher.internal.compiler.v2_1.symbols._
import commands.{expressions => commandexpressions}
import ast.convert.ExpressionConverters._

/** THIS SHOULD PROBABLY NOT BE A FUNCTION
  *
  * Created by lyonwj on 6/25/14.
  */
case object SpatialAddNode extends Function {
  def name = "spatialaddnode"

  def semanticCheck(ctx: ast.Expression.SemanticContext, invocation: ast.FunctionInvocation): SemanticCheck =
    checkMinArgs(invocation, 2) chain
      invocation.arguments.expectType(CTAny.covariant) chain
      invocation.specifyType(CTNode)

  def asCommandExpression(invocation: ast.FunctionInvocation) =
    SpatialAddNodeFunction(
      invocation.arguments(0).asCommandExpression,
      invocation.arguments(1).asCommandExpression
    )

}

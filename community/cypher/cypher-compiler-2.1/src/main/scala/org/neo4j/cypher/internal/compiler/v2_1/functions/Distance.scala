package org.neo4j.cypher.internal.compiler.v2_1.functions

import org.neo4j.cypher.internal.compiler.v2_1._
import ast.convert.ExpressionConverters._
import commands.{expressions => commandexpressions}
import symbols._

case object Distance extends Function {
  def name = "distance"

  def semanticCheck(ctx: ast.Expression.SemanticContext, invocation: ast.FunctionInvocation): SemanticCheck =
    checkMinArgs(invocation, 2) chain
    invocation.arguments.expectType(CTAny.covariant) chain
    invocation.specifyType(invocation.arguments.mergeUpTypes)

  def asCommandExpression(invocation: ast.FunctionInvocation) =
    commandexpressions.DistanceFunction(
      invocation.arguments(0).asCommandExpression,
      invocation.arguments(1).asCommandExpression
    )

}
package org.neo4j.cypher.internal.compiler.v2_1.functions

import org.neo4j.cypher.internal.compiler.v2_1._
import org.neo4j.cypher.internal.compiler.v2_1.symbols._
import commands.{expressions => commandexpressions}
import org.neo4j.cypher.internal.compiler.v2_1.spatial._
import symbols._
import ast.convert.ExpressionConverters._
/**
 * Created by lyonwj on 6/18/14.
 */
case object Intersects extends Function {
  def name = "intersects"

  def semanticCheck(ctx: ast.Expression.SemanticContext, invocation: ast.FunctionInvocation): SemanticCheck =
    checkMinArgs(invocation, 3) chain
      invocation.arguments.expectType(CTAny.covariant) chain
      invocation.specifyType(CTBoolean)

  def asCommandExpression(invocation: ast.FunctionInvocation) =
    IntersectsFunction(
      invocation.arguments(0).asCommandExpression,
      invocation.arguments(1).asCommandExpression,
      invocation.arguments(2).asCommandExpression
    )
}

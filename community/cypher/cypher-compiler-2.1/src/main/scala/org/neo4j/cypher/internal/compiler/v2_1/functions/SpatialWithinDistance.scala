package org.neo4j.cypher.internal.compiler.v2_1.functions

import org.neo4j.cypher.internal.compiler.v2_1._
import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions
import org.neo4j.cypher.internal.compiler.v2_1.functions.Distance._
import org.neo4j.cypher.internal.compiler.v2_1.symbols._
import commands.{expressions => commandexpressions}
import ast.convert.ExpressionConverters._


/**
 * Created by lyonwj on 7/11/14.
 */


  case object SpatialWithinDistance extends Function {
    def name = "spatialwithindistance"

    def semanticCheck(ctx: ast.Expression.SemanticContext, invocation: ast.FunctionInvocation): SemanticCheck =
      checkMinArgs(invocation, 2) chain
        invocation.arguments.expectType(CTAny.covariant) chain
        invocation.specifyType(CTFloat)

    def asCommandExpression(invocation: ast.FunctionInvocation) =
      commandexpressions.DistanceFunction(
        invocation.arguments(0).asCommandExpression,
        invocation.arguments(1).asCommandExpression,
        invocation.arguments(2).asCommandExpression
      )

  }



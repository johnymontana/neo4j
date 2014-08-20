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



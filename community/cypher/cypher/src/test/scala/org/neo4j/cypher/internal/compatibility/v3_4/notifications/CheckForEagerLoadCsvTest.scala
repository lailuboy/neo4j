/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
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
package org.neo4j.cypher.internal.compatibility.v3_4.notifications

import org.neo4j.cypher.internal.compiler.v3_4.planner.LogicalPlanningTestSupport
import org.neo4j.cypher.internal.frontend.v3_4.notification.EagerLoadCsvNotification
import org.neo4j.cypher.internal.ir.v3_4.NoHeaders
import org.neo4j.cypher.internal.runtime.interpreted.CSVResources
import org.neo4j.cypher.internal.util.v3_4.test_helpers.CypherFunSuite
import org.neo4j.cypher.internal.v3_4.expressions.StringLiteral
import org.neo4j.cypher.internal.v3_4.logical.plans.{AllNodesScan, Eager, LoadCSV}

class CheckForEagerLoadCsvTest extends CypherFunSuite with LogicalPlanningTestSupport {

  private val url = StringLiteral("file:///tmp/foo.csv")(pos)

  test("should notify for EagerPipe on top of LoadCsvPipe") {
    val plan =
      Eager(
        LoadCSV(
          AllNodesScan("a", Set.empty),
          url,
          "foo",
          NoHeaders,
          None,
          legacyCsvQuoteEscaping = false,
          CSVResources.DEFAULT_BUFFER_SIZE
        )
      )

    checkForEagerLoadCsv(plan) should equal(Seq(EagerLoadCsvNotification))
  }

  test("should not notify for LoadCsv on top of eager pipe") {
    val plan =
      LoadCSV(
        Eager(
          AllNodesScan("a", Set.empty)
        ),
        url,
        "foo",
        NoHeaders,
        None,
        legacyCsvQuoteEscaping = false,
        CSVResources.DEFAULT_BUFFER_SIZE
      )

    checkForEagerLoadCsv(plan) should equal(Seq.empty)
  }
}

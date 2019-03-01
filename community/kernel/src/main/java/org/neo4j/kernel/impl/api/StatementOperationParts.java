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
package org.neo4j.kernel.impl.api;

import org.neo4j.kernel.impl.api.operations.QueryRegistrationOperations;

public class StatementOperationParts
{
    private final QueryRegistrationOperations queryRegistrationOperations;

    public StatementOperationParts(
            QueryRegistrationOperations queryRegistrationOperations )
    {
        this.queryRegistrationOperations = queryRegistrationOperations;
    }

    public QueryRegistrationOperations queryRegistrationOperations()
    {
        return checkNotNull( queryRegistrationOperations, QueryRegistrationOperations.class );
    }

    public StatementOperationParts override( QueryRegistrationOperations queryRegistrationOperations )
    {
        return new StatementOperationParts(
                eitherOr( queryRegistrationOperations, this.queryRegistrationOperations, QueryRegistrationOperations.class ) );
    }

    private <T> T checkNotNull( T object, Class<T> cls )
    {
        if ( object == null )
        {
            throw new IllegalStateException( "No part of type " + cls.getSimpleName() + " assigned" );
        }
        return object;
    }

    private <T> T eitherOr( T first, T other,
            @SuppressWarnings( "UnusedParameters"/*used as type flag*/ ) Class<T> cls )
    {
        return first != null ? first : other;
    }
}

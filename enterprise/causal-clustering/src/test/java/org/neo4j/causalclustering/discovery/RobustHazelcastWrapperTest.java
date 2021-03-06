/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.causalclustering.discovery;

import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RobustHazelcastWrapperTest
{
    @Test
    public void shouldReconnectIfHazelcastConnectionInvalidated() throws Exception
    {
        // given
        HazelcastConnector connector = mock( HazelcastConnector.class );
        HazelcastInstance hzInstance = mock( HazelcastInstance.class );

        when( connector.connectToHazelcast() ).thenReturn( hzInstance );

        RobustHazelcastWrapper hzWrapper = new RobustHazelcastWrapper( connector );

        // when
        hzWrapper.perform( hz ->
        { /* nothing*/ } );

        // then
        verify( connector, times( 1 ) ).connectToHazelcast();

        // then
        try
        {
            hzWrapper.perform( hz ->
            {
                throw new com.hazelcast.core.HazelcastInstanceNotActiveException();
            } );
            fail();
        }
        catch ( HazelcastInstanceNotActiveException e )
        {
            // expected
        }

        // when
        hzWrapper.perform( hz ->
        { /* nothing*/ } );
        hzWrapper.perform( hz ->
        { /* nothing*/ } );

        // then
        verify( connector, times( 2 ) ).connectToHazelcast();
    }
}

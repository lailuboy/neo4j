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
package org.neo4j.kernel.impl.pagecache;

import java.util.function.Supplier;

import org.neo4j.helpers.Service;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.AvailabilityGuard;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.impl.logging.LogService;
import org.neo4j.kernel.impl.spi.KernelContext;
import org.neo4j.kernel.impl.transaction.state.NeoStoreFileListing;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.monitoring.Monitors;
import org.neo4j.logging.Log;
import org.neo4j.scheduler.JobScheduler;

@Service.Implementation( KernelExtensionFactory.class )
public class PageCacheWarmerKernelExtensionFactory
        extends KernelExtensionFactory<PageCacheWarmerKernelExtensionFactory.Dependencies>
{
    public interface Dependencies
    {
        JobScheduler jobScheduler();

        AvailabilityGuard availabilityGuard();

        PageCache pageCache();

        FileSystemAbstraction fileSystemAbstraction();

        NeoStoreFileListing fileListing();

        LogService logService();

        Monitors monitors();

        Config config();
    }

    public PageCacheWarmerKernelExtensionFactory()
    {
        super( "pagecachewarmer" );
    }

    @Override
    public Lifecycle newInstance( KernelContext context, Dependencies deps )
    {
        JobScheduler scheduler = deps.jobScheduler();
        AvailabilityGuard availabilityGuard = deps.availabilityGuard();
        PageCache pageCache = deps.pageCache();
        FileSystemAbstraction fs = deps.fileSystemAbstraction();
        Supplier<NeoStoreFileListing> fileListing = deps::fileListing;
        LogService logService = deps.logService();
        Log log = logService.getInternalLog( PageCacheWarmer.class );
        PageCacheWarmerMonitor monitor = deps.monitors().newMonitor( PageCacheWarmerMonitor.class );
        Config config = deps.config();
        return new PageCacheWarmerKernelExtension(
                scheduler, availabilityGuard, pageCache, fs, fileListing, log, monitor, config );
    }
}

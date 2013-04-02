/**
 * Copyright (c) 2002-2013 "Neo Technology,"
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
package org.neo4j.kernel.impl.api.index;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.neo4j.helpers.ThisShouldNotHappenError;
import org.neo4j.kernel.api.KernelException;
import org.neo4j.kernel.api.index.IndexNotFoundKernelException;
import org.neo4j.kernel.api.index.IndexReader;
import org.neo4j.kernel.api.index.InternalIndexState;
import org.neo4j.kernel.api.index.NodePropertyUpdate;

public class FlippableIndexProxy implements IndexProxy
{
    private boolean closed;

    public static final class FlipFailedKernelException extends KernelException
    {
        public FlipFailedKernelException( String message, Throwable cause )
        {
            super( message, cause );
        }
    }
    
    private static final Callable<Void> NO_OP = new Callable<Void>()
    {
        @Override
        public Void call() throws Exception
        {
            return null;
        }
    };

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private IndexProxyFactory flipTarget;
    private IndexProxy delegate;

    public FlippableIndexProxy()
    {
        this( null );
    }

    public FlippableIndexProxy( IndexProxy originalDelegate )
    {
        this.delegate = originalDelegate;
    }
    
    @Override
    public void start() throws IOException
    {
        lock.readLock().lock();
        try
        {
            delegate.start();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void update( Iterable<NodePropertyUpdate> updates ) throws IOException
    {
        lock.readLock().lock();
        try
        {
            delegate.update( updates );
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void recover( Iterable<NodePropertyUpdate> updates ) throws IOException
    {
        // TODO Shouldn't need the lock
        lock.readLock().lock();
        try
        {
            delegate.recover( updates );
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Future<Void> drop() throws IOException
    {
        lock.readLock().lock();
        try
        {
            closed = true;
            return delegate.drop();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void force() throws IOException
    {
        lock.readLock().lock();
        try
        {
            delegate.force();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    @Override
    public IndexDescriptor getDescriptor()
    {
        lock.readLock().lock();
        try
        {
            return delegate.getDescriptor();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    @Override
    public InternalIndexState getState()
    {
        lock.readLock().lock();
        try
        {
            return delegate.getState();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Future<Void> close() throws IOException
    {
        lock.readLock().lock();
        try
        {
            closed = true;
            return delegate.close();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public IndexReader newReader() throws IndexNotFoundKernelException
    {
        lock.readLock().lock();
        try
        {
            return delegate.newReader();
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    public IndexProxy getDelegate()
    {
        return delegate;
    }

    public void setFlipTarget( IndexProxyFactory flipTarget )
    {
        lock.writeLock().lock();
        try
        {
            this.flipTarget = flipTarget;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    public void flip()
    {
        try
        {
            flip( NO_OP );
        }
        catch ( FlipFailedKernelException e )
        {
            throw new ThisShouldNotHappenError( "Mattias",
                    "Flipping without a particular action should not fail this way" );
        }
    }

    public void flip( Callable<Void> actionDuringFlip ) throws FlipFailedKernelException
    {
        flip( actionDuringFlip, delegate );
    }
    
    public void flip( Callable<Void> actionDuringFlip, IndexProxy failureFlipTarget ) throws FlipFailedKernelException
    {
        lock.writeLock().lock();
        try
        {
            assertStillOpenForBusiness();
            try
            {
                actionDuringFlip.call();
                this.delegate = flipTarget.create();
            }
            catch ( Exception e )
            {
                this.delegate = failureFlipTarget;
                throw new FlipFailedKernelException( "Failed to transition index to new context, see nested exception.", e );
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + " -> " + delegate + "[target:" + flipTarget + "]";
    }

    private void assertStillOpenForBusiness()
    {
        if ( closed )
        {
            throw new IllegalStateException( this.getClass().getSimpleName() + " has been closed. No more interactions allowed" );
        }
    }
}
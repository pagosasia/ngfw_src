/*
 * Copyright (c) 2005 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.mvvm.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.metavize.mvvm.MvvmContextFactory;
import com.metavize.mvvm.security.Tid;
import com.metavize.mvvm.tran.TransformContext;
import com.metavize.mvvm.util.TransactionWork;
import org.apache.log4j.Logger;
import org.hibernate.Session;

public class EventLogger<E extends LogEvent> implements EventManager<E>
{
    private static final int QUEUE_SIZE = 10000;
    private static final int BATCH_SIZE = QUEUE_SIZE;
    private static final int SYNC_TIME = 600000;

    private static final Map<Tid, Worker> WORKERS = new HashMap<Tid, Worker>();
    private static final Object LOG_LOCK = new Object();

    private final List<EventCache<E>> caches = new LinkedList<EventCache<E>>();
    private final TransformContext transformContext;

    private final Logger logger = Logger.getLogger(getClass());

    private volatile int limit = 100;
    private BlockingQueue<EventDesc> inputQueue;

    // constructors -----------------------------------------------------------

    public EventLogger()
    {
        this.transformContext = null;
    }

    public EventLogger(TransformContext transformContext)
    {
        this.transformContext = transformContext;
    }

    // EventManager methods ---------------------------------------------------

    public List<FilterDesc> getFilterDescs()
    {
        List<FilterDesc> l = new ArrayList<FilterDesc>(caches.size());
        for (EventCache<E> ec : caches) {
            l.add(ec.getFilterDesc());
        }

        return l;
    }

    public EventFilter<E> getFilter(String filterName)
    {
        for (EventCache<E> ec : caches) {
            if (ec.getFilterDesc().getName().equals(filterName)) {
                return ec;
            }
        }

        return null;
    }

    public List<EventFilter<E>> getFilters()
    {
        return new LinkedList<EventFilter<E>>(caches);
    }

    public void setLimit(int limit)
    {
        boolean checkCold = limit > this.limit;

        this.limit = limit;

        if (checkCold) {
            for (EventCache<E> c : caches) {
                c.checkCold();
            }
        }
    }

    public int getLimit()
    {
        return limit;
    }

    // public methods --------------------------------------------------------

    public EventCache addEventHandler(EventHandler<E> eventHandler)
    {
        EventCache<E> ec = new EventCache<E>(this, eventHandler);
        caches.add(ec);

        return ec;
    }

    public void log(E e)
    {
        if (!inputQueue.offer(new EventDesc(this, e))) {
            logger.warn("dropping logevent: " + e);
        }
    }

    public void start()
    {
        Tid tid = null == transformContext ? null : transformContext.getTid();
        synchronized (WORKERS) {
            Worker w = WORKERS.get(tid);
            if (null == w) {
                w = new Worker(transformContext);
                WORKERS.put(tid, w);
                w.start();
            }

            this.inputQueue = w.getInputQueue();
        }
    }

    public void stop()
    {
        Tid tid = null == transformContext ? null : transformContext.getTid();
        synchronized (WORKERS) {
            inputQueue = null;
            Worker w = WORKERS.get(tid);
            w.stop();
            if (!w.isLive()) {
                WORKERS.remove(tid);
            }
        }
    }

    // package protected methods ----------------------------------------------

    TransformContext getTransformContext()
    {
        return transformContext;
    }

    private void doLog(LogEvent e)
    {
        for (EventCache<E> ec : caches) {
            ec.log((E)e);
        }
    }

    // private classes --------------------------------------------------------

    private static class Worker implements Runnable
    {
        private final List<LogEvent> logQueue = new LinkedList<LogEvent>();
        private final TransformContext transformContext;
        private final BlockingQueue<EventDesc> inputQueue
            = new LinkedBlockingQueue<EventDesc>();
        private final String tag;

        private final SyslogManager syslogManager = MvvmContextFactory
            .context().syslogManager();

        private final Logger logger = Logger.getLogger(getClass());

        private int clientCount = 0;

        private volatile Thread thread;

        // constructors -------------------------------------------------------

        Worker(TransformContext transformContext)
        {
            this.transformContext = transformContext;
            if (null == transformContext) {
                this.tag = "mvvm[0]: ";
            } else {
                this.tag = transformContext.getTransformDesc().getName()
                    + "[" + transformContext.getTid().getId() + "]: ";
            }
        }

        // Runnable methods ---------------------------------------------------

        public void run()
        {
            thread = Thread.currentThread();

            long lastSync = System.currentTimeMillis();
            long nextSync = lastSync + SYNC_TIME;

            while (null != thread) {
                long t = System.currentTimeMillis();

                if (t < nextSync) {
                    EventDesc ed;
                    try {
                        ed = inputQueue.poll(nextSync - t, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException exn) {
                        continue;
                    }

                    if (null == ed) {
                        continue;
                    }

                    LogEvent e = ed.getLogEvent();

                    logQueue.add(e);

                    try {
                        syslogManager.sendSyslog(e, tag);
                    } catch (Exception exn) { // never say die
                        logger.warn("failed to send syslog", exn);
                    }

                    ed.getEventLogger().doLog(e);
                }

                if (logQueue.size() >= BATCH_SIZE || t >= nextSync) {
                    try {
                        persist();
                    } catch (Exception exn) { // never say die
                        logger.error("something bad happened", exn);
                    }

                    lastSync = System.currentTimeMillis();
                    nextSync = lastSync + SYNC_TIME;
                }
            }

            if (0 < logQueue.size()) {
                persist();
            }
        }

        private void persist()
        {
            TransactionWork tw = new TransactionWork()
                {
                    public boolean doWork(Session s)
                    {
                        for (Iterator<LogEvent> i = logQueue.iterator();
                             i.hasNext(); ) {
                            LogEvent e = i.next();
                            s.save(e);
                            i.remove();
                        }

                        return true;
                    }
                };

            synchronized (LOG_LOCK) {
                if (null == transformContext) {
                    MvvmContextFactory.context().runTransaction(tw);
                } else {
                    transformContext.runTransaction(tw);
                }
            }
        }

        // package protected methods ------------------------------------------

        void start()
        {
            if (0 == clientCount) {
                new Thread(this).start();
            }

            clientCount++;
        }

        void stop()
        {
            clientCount--;

            if (0 == clientCount) {
                Thread t = thread;
                thread = null;
                t.interrupt();
            }
        }

        public boolean isLive()
        {
            return null != thread;
        }

        public BlockingQueue<EventDesc> getInputQueue()
        {
            return inputQueue;
        }
    }

    private static class EventDesc
    {
        private final EventLogger eventLogger;
        private final LogEvent logEvent;

        EventDesc(EventLogger eventLogger, LogEvent logEvent)
        {
            this.eventLogger = eventLogger;
            this.logEvent = logEvent;
        }

        EventLogger getEventLogger()
        {
            return eventLogger;
        }

        LogEvent getLogEvent()
        {
            return logEvent;
        }
    }
}

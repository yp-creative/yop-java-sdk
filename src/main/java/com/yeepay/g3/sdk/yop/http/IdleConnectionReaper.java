package com.yeepay.g3.sdk.yop.http;

import com.google.common.collect.Lists;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * title: 周期性检查闲置连接的守护线程<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 11:53
 */
public final class IdleConnectionReaper extends Thread {

    protected static final Logger LOGGER = Logger.getLogger(IdleConnectionReaper.class);

    /**
     * 调用间隔
     */
    private static final int PERIOD_IN_MILLIS = 60 * 1000;

    /**
     * 注册的连接, 用于周期性检查
     */
    private static List<HttpClientConnectionManager> connectionManagers = Lists.newArrayList();

    private static IdleConnectionReaper instance = new IdleConnectionReaper();

    static {
        instance.start();
    }

    private IdleConnectionReaper() {
        super("java-sdk-http-connection-reaper");
        this.setDaemon(true);
    }

    /**
     * 注册连接管理器
     *
     * @return 如果注册成功则返回 true
     */
    public static synchronized boolean registerConnectionManager(HttpClientConnectionManager connectionManager) {
        if (instance == null) {
            return false;
        }
        return connectionManagers.add(connectionManager);
    }

    /**
     * 注销连接管理器
     *
     * @return 如果注销成功则返回 true
     */
    public static synchronized boolean removeConnectionManager(HttpClientConnectionManager connectionManager) {
        if (instance == null) {
            return false;
        }
        return connectionManagers.remove(connectionManager);
    }

    @Override
    public void run() {
        while (this.isInterrupted()) {
            try {
                Thread.sleep(PERIOD_IN_MILLIS);

                // Copy the list of managed ConnectionManagers to avoid possible
                // ConcurrentModificationExceptions if registerConnectionManager or
                // removeConnectionManager are called while we're iterating (rather
                // than block/lock while this loop executes).
                List<HttpClientConnectionManager> connectionManagers = null;
                synchronized (IdleConnectionReaper.class) {
                    connectionManagers = Lists.newArrayList(connectionManagers);
                }
                for (HttpClientConnectionManager connectionManager : connectionManagers) {
                    // When we release connections, the connection manager leaves them
                    // open so they can be reused. We want to close out any idle
                    // connections so that they don't sit around in CLOSE_WAIT.
                    try {
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);
                    } catch (Throwable t) {
                        LOGGER.warn("Unable to close idle connections", t);
                    }
                }
            } catch (Throwable t) {
                LOGGER.debug("Reaper thread: ", t);
            }
        }
    }

    public static synchronized boolean shutdown() {
        if (instance != null) {
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
            return true;
        }
        return false;
    }

}

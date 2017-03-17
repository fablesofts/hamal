/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.assist;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author xieruidong 2013年11月4日 上午11:04:49
 */
public class ProcessQueue<T> {

    private static final Object            PRESENT  = new Object();
    private LRULinkedHashMap<Long, Object> history;                             // 记录一下最近分配出去的processId，容量必须>当前并行度
    private PriorityQueue<Long>            tables   = new PriorityQueue<Long>();
    private ReentrantLock                  lock     = new ReentrantLock();
    private Condition                      notEmpty = lock.newCondition();

    public ProcessQueue(int historySize){
        history = new LRULinkedHashMap<Long, Object>(historySize);
    }

    public Long take() throws InterruptedException {
        try {
            lock.lockInterruptibly();
            Long result = null;
            do {
                if (tables.size() == 0) {
                    notEmpty.await();
                }

                result = tables.poll();
            } while (result == null);

            history.put(result, PRESENT);
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(Long processId) {
        lock.lock();
        try {
            if (contains(processId)) {
                return false;
            }

            int size = tables.size();
            // tables.addLast(processId);
            tables.add(processId);// 添加记录
            if (size == 0) {
                notEmpty.signalAll();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Long processId) {
        lock.lock();
        try {
            boolean result = tables.remove(processId);
            if (result) {
                history.put(processId, PRESENT); // 记录一下到历史记录
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            tables.clear();
            history.clear();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(Long processId) {
        return tables.contains(processId) || history.containsKey(processId);
    }

    public Object[] toArray() {
        return tables.toArray();
    }

    public int size() {
        return tables.size();
    }

}

/**
 * 简单的继承实现LRU算法对象,注意需要控制多线程
 */
class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long  serialVersionUID    = 1827912970480911024L;

    private final int          maxCapacity;

    private static final float DEFAULT_LOAD_FACTOR = 1f;

    public LRULinkedHashMap(int maxCapacity){
        super(maxCapacity, DEFAULT_LOAD_FACTOR, false);
        this.maxCapacity = maxCapacity;
    }

    protected boolean removeEldestEntry(Entry<K,V> eldest) {
        return (size() > this.maxCapacity);
    }
}

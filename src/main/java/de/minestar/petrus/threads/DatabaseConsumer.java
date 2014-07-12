/*
 * Copyright (C) 2014 MineStar.de 
 * 
 * This file is part of Petrus.
 * 
 * Petrus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Petrus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Petrus.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.petrus.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import com.j256.ormlite.dao.Dao;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.core.PetrusCore;

public class DatabaseConsumer<T> implements Runnable {

    /**
     * The queue for holding the items. This is sync safe
     */
    private LinkedBlockingQueue<T> queue;

    /**
     * Access to database
     */
    private Dao<T, ?> dao;

    private int bufferSize = 50;

    public DatabaseConsumer(Dao<T, ?> DAO) {
        this.queue = new LinkedBlockingQueue<>();
        this.dao = DAO;
    }

    /**
     * Set the amount of items held in the queue until the queue is flushed and
     * items are persisted
     * 
     * @param bufferSize
     *            Amount of items to hold in the queue
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {

        // Time to flush?
        if (queue.size() >= bufferSize)
            flush();
    }

    /**
     * Add an item to the queue (producing it).
     * 
     * @param item
     *            The item to persisted to the database
     */
    public void produce(T item) {
        this.queue.offer(item);
    }

    /**
     * Force the queue to flush (if there are items in it)
     */
    public void flush() {
        if (queue.isEmpty())
            return;

        // Copy all items from the queue to this temporary list
        // Reason: This is faster and threads adding items are not blocked
        final List<T> items = new ArrayList<>(queue.size());
        queue.drainTo(items);

        try {
            dao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (T item : items) {
                        dao.create(item);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Flushing queue");
        }
    }

}

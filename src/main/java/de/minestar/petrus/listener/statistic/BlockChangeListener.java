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

package de.minestar.petrus.listener.statistic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.petrus.statistics.BlockBreakStatistic;
import de.minestar.petrus.statistics.BlockPlaceStatistic;
import de.minestar.petrus.threads.DatabaseConsumer;

public class BlockChangeListener implements Listener {

    private DatabaseConsumer<BlockBreakStatistic> blockBreakQueue;
    private DatabaseConsumer<BlockPlaceStatistic> blockPlaceQueue;

    public BlockChangeListener(DatabaseConsumer<BlockBreakStatistic> blockBreakQueue, DatabaseConsumer<BlockPlaceStatistic> blockPlaceQueue) {
        this.blockBreakQueue = blockBreakQueue;
        this.blockPlaceQueue = blockPlaceQueue;

    }

    private Location loc = new Location(null, 0.0, 0.0, 0.0);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player == null || block == null)
            return;
        block.getLocation(loc);
        Material mat = block.getType();
        // This can be null for the mod blocks?
        String blockName = mat != null ? mat.toString() : null;
        blockBreakQueue.produce(new BlockBreakStatistic(blockName, player.getName(), loc.getX(), loc.getY(), loc.getY(), loc.getWorld().getName()));

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player == null || block == null)
            return;

        block.getLocation(loc);
        Material mat = block.getType();
        // This can be null for the mod blocks?
        String blockName = mat != null ? mat.toString() : null;
        blockPlaceQueue.produce(new BlockPlaceStatistic(blockName, player.getName(), loc.getX(), loc.getY(), loc.getY(), loc.getWorld().getName()));

    }
}

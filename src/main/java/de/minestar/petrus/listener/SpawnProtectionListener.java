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

package de.minestar.petrus.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.minestar.petrus.common.StartPosition;

public class SpawnProtectionListener implements Listener {

    private StartPosition spawnLocation;
    private int spawnRadius;
    private String spawnWorldName;

    public SpawnProtectionListener(String spawnWorldName, StartPosition spawnLocation, int spawnRadius) {
        this.spawnWorldName = spawnWorldName;
        this.spawnLocation = spawnLocation;
        this.spawnRadius = spawnRadius;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isAtSpawn(event.getBlock()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && isAtSpawn(event.getClickedBlock()))
            event.setCancelled(true);
    }

    // Container for the current location
    private Location blockLocation = new Location(null, 0, 0, 0);

    private boolean isAtSpawn(Block block) {
        block.getLocation(blockLocation);
        if (!blockLocation.getWorld().getName().equals(spawnWorldName))
            return false;

        int xDiff = (int) Math.abs(spawnLocation.getX() - blockLocation.getX());
        int zDiff = (int) Math.abs(spawnLocation.getZ() - blockLocation.getZ());
//        System.out.println(xDiff + " " + zDiff + " " + spawnRadius);

        return xDiff <= spawnRadius && zDiff <= spawnRadius;
    }
}

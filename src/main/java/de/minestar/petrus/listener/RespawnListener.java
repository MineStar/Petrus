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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.minestar.petrus.common.Team;
import de.minestar.petrus.core.PetrusCore;

public class RespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Location respawnLocation = event.getRespawnLocation();
        Location spawnLocation = PetrusCore.CONFIG.spawnPosition();
        double xDiff = Math.abs(respawnLocation.getX() - spawnLocation.getX());
        double yDiff = Math.abs(respawnLocation.getY() - spawnLocation.getY());
        double zDiff = Math.abs(respawnLocation.getZ() - spawnLocation.getZ());
        if (xDiff <= 2.0 && yDiff <= 2.0 && zDiff <= 2.0) {
            Player player = event.getPlayer();
            Team team = PetrusCore.TEAM_MANAGER.getPlayersTeam(player.getName());
            if (team != null) {
                respawnLocation = team.getStartPosition();
                respawnLocation.setWorld(spawnLocation.getWorld());
                event.setRespawnLocation(respawnLocation);
            }
        }
    }
}

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

import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.j256.ormlite.dao.Dao;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.core.PetrusCore;
import de.minestar.petrus.statistics.EntityDeathStatistic;
import de.minestar.petrus.statistics.PlayerDeathStatistic;

public class DeathListener implements Listener {

    private Dao<PlayerDeathStatistic, ?> playerDeathDAO;
    private Dao<EntityDeathStatistic, ?> entityDeathDAO;

    public DeathListener(Dao<PlayerDeathStatistic, ?> playerDeathDAO, Dao<EntityDeathStatistic, ?> entityDeathDAO) {
        this.playerDeathDAO = playerDeathDAO;
        this.entityDeathDAO = entityDeathDAO;
    }

    private Location loc = new Location(null, 0.0, 0.0, 0.0);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity ent = event.getEntity();
        EntityDamageEvent lastDamageCause = ent.getLastDamageCause();
        if (lastDamageCause != null) {
            try {
                DamageCause reason = lastDamageCause.getCause();
                ent.getLocation(loc);
                if (ent instanceof Player) {
                    Player player = (Player) ent;
                    playerDeathDAO.create(new PlayerDeathStatistic(player.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName(), reason.toString()));
                } else {
                    entityDeathDAO.create(new EntityDeathStatistic(ent.getType().toString(), ent.getCustomName(), loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName(), reason.toString()));
                }
            } catch (SQLException e) {
                ConsoleUtils.printException(e, PetrusCore.NAME, "Statistic for death of entity");
            }
        }
    }
}

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.j256.ormlite.dao.Dao;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.core.PetrusCore;
import de.minestar.petrus.statistics.LoginStatistic;
import de.minestar.petrus.statistics.LogoutStatistic;

public class SessionListener implements Listener {

    private Dao<LoginStatistic, ?> loginDAO;
    private Dao<LogoutStatistic, ?> logoutDAO;

    public SessionListener(Dao<LoginStatistic, ?> loginDAO, Dao<LogoutStatistic, ?> logoutDAO) {
        this.loginDAO = loginDAO;
        this.logoutDAO = logoutDAO;
    }

    private Location loc = new Location(null, 0.0, 0.0, 0.0);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getLocation(loc);

        try {
            loginDAO.create(new LoginStatistic(player.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName()));
        } catch (SQLException e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Can't log login!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getLocation(loc);

        try {
            logoutDAO.create(new LogoutStatistic(player.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName()));
        } catch (SQLException e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Can't log login!");
        }
    }
}

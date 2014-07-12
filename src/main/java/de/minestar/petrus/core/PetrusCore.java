/*
 * Copyright (C) 2014 MineStar.de 
 * 
 * This file is part of BlackGate.
 * 
 * BlackGate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * BlackGate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BlackGate.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.petrus.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.MysqlDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.commands.PetrusCommand;
import de.minestar.petrus.configuration.PetrusConfiguration;
import de.minestar.petrus.listener.JoinListener;
import de.minestar.petrus.listener.RespawnListener;
import de.minestar.petrus.listener.SpawnDeathListener;
import de.minestar.petrus.listener.SpawnProtectionListener;
import de.minestar.petrus.listener.statistic.BlockChangeListener;
import de.minestar.petrus.listener.statistic.DeathListener;
import de.minestar.petrus.listener.statistic.SessionListener;
import de.minestar.petrus.statistics.BlockBreakStatistic;
import de.minestar.petrus.statistics.BlockPlaceStatistic;
import de.minestar.petrus.statistics.EntityDeathStatistic;
import de.minestar.petrus.statistics.LoginStatistic;
import de.minestar.petrus.statistics.LogoutStatistic;
import de.minestar.petrus.statistics.PlayerDeathStatistic;
import de.minestar.petrus.team.TeamManager;
import de.minestar.petrus.threads.DatabaseConsumer;

public class PetrusCore extends AbstractCore {

    public static Plugin PLUGIN;

    public final static String NAME = "Petrus";
    public static File CONFIG_FILE;

    public static PetrusConfiguration CONFIG;

    public static World SPAWN_WORLD;

    public static TeamManager TEAM_MANAGER;

    private ConnectionSource databaseConnection;

    public static Gson JSON;

    private DatabaseConsumer<BlockBreakStatistic> blockBreakQueue;
    private DatabaseConsumer<BlockPlaceStatistic> blockPlaceQueue;

    public PetrusCore() {
        super(NAME);
        JSON = new GsonBuilder().setPrettyPrinting().create();
        PLUGIN = this;
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {

        CONFIG_FILE = new File(dataFolder, "generalConfig.json");
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            CONFIG = JSON.fromJson(reader, PetrusConfiguration.class);
            SPAWN_WORLD = Bukkit.getWorld(CONFIG.spawnWorldName());
        } catch (IOException e) {
            ConsoleUtils.printException(e, NAME, "Error loading " + CONFIG_FILE);
            return false;
        }
        return super.loadingConfigs(dataFolder);
    }

    @Override
    protected boolean createManager() {

        TEAM_MANAGER = new TeamManager(CONFIG.teams());
        try {

            MysqlDatabaseType type = new MysqlDatabaseType();
            // use myISAM , because we don't need things like transaction safe
            // stores
            type.setCreateTableSuffix("ENGINE=MyISAM");
            // TODO: Read connection infos from config
            databaseConnection = new JdbcConnectionSource("jdbc:mysql://192.168.1.29:3306/petrus?autoReconnect=true", "petrus", "1q2w3e4r", type);
//            databaseConnection.
        } catch (SQLException e) {
            ConsoleUtils.printException(e, NAME, "Can't open database connection");
        }
        return super.createManager();
    }

    @Override
    protected boolean createCommands() {

        this.cmdList = new CommandList(NAME, new PetrusCommand("/petrus", "", ""));
        return super.createCommands();
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(new SpawnProtectionListener(CONFIG.spawnWorldName(), CONFIG.spawnPosition(), CONFIG.spawnRadius()), this);
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new SpawnDeathListener(CONFIG.spawnPosition()), this);
        pm.registerEvents(new RespawnListener(), this);

        // statistic events
        try {
            // Login and Logout Statistics
            TableUtils.createTableIfNotExists(databaseConnection, LoginStatistic.class);
            TableUtils.createTableIfNotExists(databaseConnection, LogoutStatistic.class);
            pm.registerEvents(new SessionListener(DaoManager.createDao(databaseConnection, LoginStatistic.class), DaoManager.createDao(databaseConnection, LogoutStatistic.class)), this);

            // Death statistics
            TableUtils.createTableIfNotExists(databaseConnection, PlayerDeathStatistic.class);
            TableUtils.createTableIfNotExists(databaseConnection, EntityDeathStatistic.class);
            pm.registerEvents(new DeathListener(DaoManager.createDao(databaseConnection, PlayerDeathStatistic.class), DaoManager.createDao(databaseConnection, EntityDeathStatistic.class)), this);

            // Block break and place statistics
            blockBreakQueue = new DatabaseConsumer<BlockBreakStatistic>(DaoManager.createDao(databaseConnection, BlockBreakStatistic.class));
            blockPlaceQueue = new DatabaseConsumer<BlockPlaceStatistic>(DaoManager.createDao(databaseConnection, BlockPlaceStatistic.class));

            TableUtils.createTableIfNotExists(databaseConnection, BlockBreakStatistic.class);
            TableUtils.createTableIfNotExists(databaseConnection, BlockPlaceStatistic.class);
            pm.registerEvents(new BlockChangeListener(blockBreakQueue, blockPlaceQueue), this);
        } catch (Exception e) {
            ConsoleUtils.printException(e, NAME, "Creating statistic listeners!");
        }
        return true;
    }

    @Override
    protected boolean createThreads() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, blockBreakQueue, 20 * 10, 20 * 5);
        Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, blockPlaceQueue, 20 * 10, 20 * 5);
        return super.createThreads();
    }

    @Override
    protected boolean commonDisable() {
        try {
            // Store last save data in queue in database before shutdown
            blockBreakQueue.flush();
            blockPlaceQueue.flush();

            databaseConnection.close();
        } catch (Exception e) {
            ConsoleUtils.printException(e, NAME, "Closing database session");
        }
        return super.commonDisable();
    }
}

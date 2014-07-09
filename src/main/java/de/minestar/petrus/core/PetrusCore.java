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

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.commands.PetrusCommand;
import de.minestar.petrus.configuration.PetrusConfiguration;
import de.minestar.petrus.listener.JoinListener;
import de.minestar.petrus.listener.SpawnDeathListener;
import de.minestar.petrus.listener.SpawnProtectionListener;
import de.minestar.petrus.team.TeamManager;

public class PetrusCore extends AbstractCore {

    public static Plugin PLUGIN;

    public final static String NAME = "Petrus";
    public static File CONFIG_FILE;

    public static PetrusConfiguration CONFIG;

    public static World SPAWN_WORLD;
    
    public static TeamManager TEAM_MANAGER;

    public static Gson JSON;

    public static final String META_DATA_INVITATION_ACCEPTED = "acceptedInvitation";

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

        return true;
    }
}

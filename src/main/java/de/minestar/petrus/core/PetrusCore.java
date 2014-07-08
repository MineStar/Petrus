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

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.plugin.PluginManager;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.configuration.PetrusConfiguration;
import de.minestar.petrus.listener.SpawnProtectionListener;

public class PetrusCore extends AbstractCore {

    public final static String NAME = "Petrus";

    public static PetrusConfiguration CONFIG;

    public PetrusCore() {
        super(NAME);
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {

        try (Reader reader = new FileReader(new File(dataFolder, "generalConfig.json"))) {
            Gson gson = new GsonBuilder().create();
            CONFIG = gson.fromJson(reader, PetrusConfiguration.class);
            System.out.println(CONFIG.startPositions());
        } catch (IOException e) {
            ConsoleUtils.printException(e, NAME, "Error loading generalConfig.json");
            return false;
        }
        // TODO Auto-generated method stub
        return super.loadingConfigs(dataFolder);
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(new SpawnProtectionListener(CONFIG.spawnWorldName(), CONFIG.spawnPosition(), CONFIG.spawnRadius()), this);

        return true;
    }
}

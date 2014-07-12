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

package de.minestar.petrus.configuration;

import org.bukkit.Location;

import de.minestar.petrus.core.PetrusCore;

public class PetrusConfiguration {

    private PetrusConfiguration() {
        // For Serialization
    }

    private Location spawnPosition;
    private int spawnRadius;
    private String spawnWorldName;

    public Location spawnPosition() {
        Location loc = spawnPosition.clone();
        loc.setWorld(PetrusCore.SPAWN_WORLD);
        return loc;
    }

    public int spawnRadius() {
        return spawnRadius;
    }

    public String spawnWorldName() {
        return spawnWorldName;
    }
}

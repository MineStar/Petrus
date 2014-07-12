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

package de.minestar.petrus.statistics;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class EntityDeathStatistic {

    @DatabaseField
    private String entityType;

    @DatabaseField(canBeNull = true)
    private String entityName;

    @DatabaseField
    private double x;
    @DatabaseField
    private double y;
    @DatabaseField
    private double z;
    @DatabaseField
    private String worldName;

    @DatabaseField
    private Date timestamp;

    @DatabaseField
    private String reason;

    protected EntityDeathStatistic() {
        // Needed for ormlite
    }

    public EntityDeathStatistic(String entityType, String entityName, double x, double y, double z, String worldName, String reason) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.timestamp = new Date();
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "EntityDeathStatistic [entityType=" + entityType + ", entityName=" + entityName + ", x=" + x + ", y=" + y + ", z=" + z + ", worldName=" + worldName + ", timestamp=" + timestamp + ", reason=" + reason + "]";
    }

}

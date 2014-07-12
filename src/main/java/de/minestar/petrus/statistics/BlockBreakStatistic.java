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
public class BlockBreakStatistic {

    @DatabaseField(canBeNull = true)
    private String blockName;

    @DatabaseField
    private String userName;

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

    protected BlockBreakStatistic() {
        // Needed for ormLite
    }

    public BlockBreakStatistic(String blockName, String userName, double x, double y, double z, String worldName) {
        this.blockName = blockName;
        this.userName = userName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return "BlockBreakStatistic [blockName=" + blockName + ", userName=" + userName + ", x=" + x + ", y=" + y + ", z=" + z + ", worldName=" + worldName + ", timestamp=" + timestamp + "]";
    }

}

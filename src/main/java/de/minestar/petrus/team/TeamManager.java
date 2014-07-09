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

package de.minestar.petrus.team;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.petrus.common.Team;
import de.minestar.petrus.core.PetrusCore;

public class TeamManager {

    private List<Team> teams;

    public TeamManager(List<Team> teams) {
        this.teams = teams;
    }

    public Team getTeamByName(String teamName) {
        for (Team team : teams) {
            if (teamName.equals(team.getName()))
                return team;
        }
        return null;
    }

    public void setTeamLeader(Player player, Team team) {
        team.becomeLeader(player);
        update();
    }

    public void addAspirant(Player player, Team team) {
        team.addAsAspirant(player);
        update();
    }

    public void convertToMember(String playerName, Team team) {
        team.convertToMember(playerName);
        update();
    }

    // TODO: Ugly hack to write the complete CONFIG FILE
    // Need to find a way to serialize lists with GSON
    // Throws error using MCPC+
    private void update() {
        try (Writer w = new FileWriter(PetrusCore.CONFIG_FILE)) {
            PetrusCore.JSON.toJson(PetrusCore.CONFIG, w);
        } catch (IOException e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Can't update config");
        }
    }

    public List<Team> getTeams() {
        return teams;
    }

}

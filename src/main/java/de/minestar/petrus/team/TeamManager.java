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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.petrus.common.Team;
import de.minestar.petrus.core.PetrusCore;

public class TeamManager {

    private static final String TEAM_FILENAME = "teams.json";

    private File teamFile;

    private List<Team> teams;

    public TeamManager(File dataFolder) {
        this.teams = loadTeams(dataFolder);
    }

    private List<Team> loadTeams(File dataFolder) {
        this.teamFile = new File(dataFolder, TEAM_FILENAME);
        try (Reader r = new FileReader(teamFile)) {
            return PetrusCore.JSON.fromJson(r, Teams.class).teams;
        } catch (Exception e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Loading teams");
            return Collections.emptyList();
        }
    }

    // Ugly hack to serialze a list of items, because of guava10 in mcpc does
    // not support this...
    public static class Teams {
        private List<Team> teams;

    }

    public Team getTeamByName(String teamName) {
        for (Team team : teams) {
            if (teamName.equalsIgnoreCase(team.getName()))
                return team;
        }
        return null;
    }

    public Team getPlayersTeam(String player) {
        for (Team team : teams) {
            if (team.isMember(player))
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

    public void acceptOfflineMember(String offlinePlayerName, Team team) {
        team.addToPendingMembers(offlinePlayerName);
        update();
    }

    public void finishPendingMember(Player player, Team team) {
        team.finishedPendingMember(player.getName());
        startGame(player, team);
        update();
    }

    public Team pendingMembersTeam(Player player) {
        for (Team team : teams) {
            if (team.isPendingMember(player.getName())) {
                return team;
            }
        }

        return null;
    }

    public void startGame(Player player, Team team) {
        Location startPosition = team.getStartPosition();
        startPosition.setX(startPosition.getX());
        startPosition.setY(startPosition.getY());
        startPosition.setZ(startPosition.getZ());
        startPosition.setWorld(PetrusCore.SPAWN_WORLD);
        player.teleport(startPosition, TeleportCause.COMMAND);

        player.setBedSpawnLocation(startPosition, true);

        // Trooolllooooloo
        PlayerUtils.sendInfo(player, PetrusCore.NAME, "Viel Spass wuenscht Ugly" + generateRandomBrand() + ".");
    }

    private Random rand = new Random();
    private String[] brands = {"Bier", "Saft", "Rasenmaeher", "Grills", "Saurier", "UBoote", "Sterne", "Games", "UndSoehne", "UndToechter"};

    private String generateRandomBrand() {
        return brands[rand.nextInt(brands.length)];
    }

    private void update() {
        try (Writer w = new FileWriter(teamFile)) {
            Teams temponaryHack = new Teams();
            temponaryHack.teams = teams;
            PetrusCore.JSON.toJson(temponaryHack, Teams.class);
        } catch (IOException e) {
            ConsoleUtils.printException(e, PetrusCore.NAME, "Can't update config");
        }
    }

    public List<Team> getTeams() {
        return teams;
    }

}

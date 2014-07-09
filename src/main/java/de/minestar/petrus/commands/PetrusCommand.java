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

package de.minestar.petrus.commands;

import static de.minestar.petrus.core.PetrusCore.TEAM_MANAGER;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.petrus.common.StartPosition;
import de.minestar.petrus.common.Team;
import de.minestar.petrus.core.PetrusCore;

public class PetrusCommand extends AbstractExtendedCommand {

    public PetrusCommand(String syntax, String arguments, String node) {
        super(PetrusCore.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        if (args.length == 0) {
            showTeams(player);
        } else if (args.length == 1) {
            chooseTeam(player, args[0]);
        } else if (args.length == 2 && args[0].equals("accept")) {
            acceptInvitation(player, args[1]);
        }

    }

    private void showTeams(Player player) {
        PlayerUtils.sendInfo(player, pluginName, "Zur Auswahl '/petrus TEAMNAME' eingeben");
        PlayerUtils.sendInfo(player, pluginName, "Beispiel: '/petrus yellow'");
        PlayerUtils.sendInfo(player, pluginName, "=======================================");
        List<Team> teams = TEAM_MANAGER.getTeams();
        for (Team team : teams) {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("'").append(team.getName()).append("' - ");

            if (team.hasLeader())
                sBuilder.append("'" + team.getLeaderName() + "'");
            else
                sBuilder.append(" Noch Fuehrungslos");
            PlayerUtils.sendInfo(player, pluginName, sBuilder.toString());
        }
    }

    private void chooseTeam(Player player, String teamName) {

        for (Team tmpTeams : TEAM_MANAGER.getTeams()) {
            if (tmpTeams.isMember(player.getName())) {
                PlayerUtils.sendError(player, pluginName, "Du bist bereits in einem Team! Gierig!");
                return;
            }
        }

        Team team = TEAM_MANAGER.getTeamByName(teamName);
        if (team == null) {
            PlayerUtils.sendError(player, pluginName, "Kein Team gefunden fuer '" + teamName + "'!");
            return;
        }

        if (team.hasLeader()) {
            becomeAspirant(player, team);
        } else {
            TEAM_MANAGER.setTeamLeader(player, team);
            PlayerUtils.sendSuccess(player, pluginName, "Du bist nun Leader vom Team '" + team.getName() + "' !");
            PlayerUtils.sendInfo(player, pluginName, "Mit '/petrus accept NAME' kannst du Einladungen annehmen.");
            startGame(player, team);
        }
    }

    private void becomeAspirant(Player player, Team team) {
        PlayerUtils.sendInfo(player, pluginName, "Team '" + team.getName() + "' hat als Anfuhrer '" + team.getLeaderName() + "'.");
        PlayerUtils.sendInfo(player, pluginName, "Ihm wurde eine Einladung gesendet. Bitte warte, bis er diese annimmt!");
        team.addAsAspirant(player);

        Player teamLeader = Bukkit.getPlayerExact(team.getLeaderName());
        if (teamLeader != null && teamLeader.isOnline()) {
            PlayerUtils.sendInfo(player, pluginName, "Spieler '" + player.getName() + "' moechte deinem Team beitreten.");
            PlayerUtils.sendInfo(player, pluginName, "Akzeptiere seine Anfrage mit '/petrus accept " + player.getName() + "'");
        }
    }

    private void acceptInvitation(Player player, String playerName) {
        List<Team> teams = TEAM_MANAGER.getTeams();
        Team team = null;
        for (Team tmp : teams) {
            if (tmp.isLeader(player)) {
                team = tmp;
                break;
            }
        }
        // player is no leader
        if (team == null) {
            PlayerUtils.sendError(player, pluginName, "Du bist kein Anfuehrer!");
            return;
        }

        if (!team.isAspirant(playerName)) {
            PlayerUtils.sendError(player, pluginName, "Spieler '" + playerName + "' ist kein Anwärter!");
            return;
        }

        TEAM_MANAGER.convertToMember(playerName, team);
        PlayerUtils.sendSuccess(player, pluginName, "Spieler '" + playerName + "' ist nun Teammitglied");

        Player newMember = Bukkit.getPlayerExact(playerName);
        if (newMember != null && newMember.isOnline()) {
            PlayerUtils.sendSuccess(newMember, pluginName, "Deine Einladung wurde akzeptiert!");
            startGame(newMember, team);
        } else if (newMember != null) {
            // set meta data for player to know at his next login, that his
            // invitation was accepted
            newMember.setMetadata("acceptedInvitation", new FixedMetadataValue(PetrusCore.PLUGIN, team.getName()));
        }

    }

    private void startGame(Player player, Team team) {
        StartPosition startPosition = team.getStartPosition();
        Location loc = player.getLocation();
        loc.setX(startPosition.getX());
        loc.setY(startPosition.getY());
        loc.setZ(startPosition.getZ());
        player.teleport(loc, TeleportCause.COMMAND);
        player.setBedSpawnLocation(loc, true); // TODO: Is this right?

        // Trooolllooooloo
        PlayerUtils.sendInfo(player, pluginName, "Viel Spass wuenscht Ugly" + generateRandomBrand() + ".");
    }

    private Random rand = new Random();
    private String[] brands = {"Bier", "Saft", "Rasenmaeher", "Grills", "Saurier", "UBoote", "Sterne", "Games", "Soehne", "Toechter"};

    private String generateRandomBrand() {
        return brands[rand.nextInt(brands.length)];
    }
}

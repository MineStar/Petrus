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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
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

        Team tmpTeam = TEAM_MANAGER.getPlayersTeam(player.getName());
        if (tmpTeam != null) {
            PlayerUtils.sendInfo(player, pluginName, "Du bist im Team '" + tmpTeam.getName() + "'!");
        } else {
            PlayerUtils.sendInfo(player, pluginName, "Zur Auswahl '/petrus TEAMNAME' eingeben");
            PlayerUtils.sendInfo(player, pluginName, "Beispiel: '/petrus yellow'");
        }
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

        Team tmpTeam = TEAM_MANAGER.getPlayersTeam(player.getName());
        if (tmpTeam != null) {
            PlayerUtils.sendError(player, pluginName, "Du bist bereits im Team '" + tmpTeam.getName() + "'!");
            return;
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
            TEAM_MANAGER.startGame(player, team);
        }
    }
    private void becomeAspirant(Player player, Team team) {
        PlayerUtils.sendInfo(player, pluginName, "Team '" + team.getName() + "' hat als Anfuhrer '" + team.getLeaderName() + "'.");
        PlayerUtils.sendInfo(player, pluginName, "Ihm wurde eine Einladung gesendet. Bitte warte, bis er diese annimmt!");
        team.addAsAspirant(player);

        Player teamLeader = Bukkit.getPlayerExact(team.getLeaderName());
        if (teamLeader != null && teamLeader.isOnline()) {
            PlayerUtils.sendInfo(teamLeader, pluginName, "Spieler '" + player.getName() + "' moechte deinem Team beitreten.");
            PlayerUtils.sendInfo(teamLeader, pluginName, "Akzeptiere seine Anfrage mit '/petrus accept " + player.getName() + "'");
        }
    }

    private void acceptInvitation(Player player, String newMemberName) {
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

        if (!team.isAspirant(newMemberName)) {
            PlayerUtils.sendError(player, pluginName, "Spieler '" + newMemberName + "' ist kein Anwärter!");
            return;
        }

        TEAM_MANAGER.convertToMember(newMemberName, team);
        PlayerUtils.sendSuccess(player, pluginName, "Spieler '" + newMemberName + "' ist nun Teammitglied");

        Player newMember = Bukkit.getPlayerExact(newMemberName);

        if (newMember != null && newMember.isOnline()) {
            PlayerUtils.sendSuccess(newMember, pluginName, "Deine Einladung wurde akzeptiert!");
            TEAM_MANAGER.startGame(newMember, team);
        } else {
            TEAM_MANAGER.acceptOfflineMember(newMemberName, team);
        }
    }

}

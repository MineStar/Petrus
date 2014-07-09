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

package de.minestar.petrus.common;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Team {

    private String name;
    private Location startPosition;

    private String leaderName;
    private Set<String> members;
    private Set<String> aspirants;
    private Set<String> pendingMembers;

    private Team() {
        // For Serialization
        members = new HashSet<>();
        aspirants = new HashSet<>();
        pendingMembers = new HashSet<>();
    }

    /**
     * @return The name of the team - should be unique
     */
    public String getName() {
        return name;
    }

    /**
     * @return The start position of the team, where every player is teleported
     *         to after choosing this team.
     */
    public Location getStartPosition() {
        return startPosition.clone();
    }

    /**
     * @return The leader name. The leader is the first member of the team and
     *         rule about who is going to be a member
     */
    public String getLeaderName() {
        return leaderName;
    }

    /**
     * @param player
     *            The player itself
     * @return <code>true</code> if, and only if, the player has the same name
     *         as the leader of the team. Otherwise <code>false</code>
     */
    public boolean isLeader(Player player) {
        return player.getName().equals(leaderName);
    }

    /**
     * @return <code>true</code> if, and only if, the team has a leader.
     *         Otherwise <code>false</code>
     */
    public boolean hasLeader() {
        return leaderName != null;
    }

    /**
     * The player is now the leader of the team. Every team can has only one
     * player and once a team leader is assigned, no one can be leader anymore!
     * 
     * @param player
     *            The player to become the leader
     * @return <code>true</code> if, and only if, this team has no leader at the
     *         moment. Otherwise <code>false</code>.
     */
    public boolean becomeLeader(Player player) {
        if (hasLeader())
            return false;

        this.leaderName = player.getName();
        return true;
    }

    /**
     * Add the player as an aspirant. He isn't a member and have to wait until
     * the leader accept his request.
     * 
     * @param aspirant
     *            The player sending the request to become a member
     */
    public void addAsAspirant(Player aspirant) {
        this.aspirants.add(aspirant.getName());
    }

    /**
     * Check, if the player has send a request to become a member.
     * 
     * @param playerName
     *            The playername
     * @return <code>true</code> if, and only if, the player has send a request
     *         to the leader. Otherwise <code>false</code>
     */
    public boolean isAspirant(String playerName) {
        return aspirants.contains(playerName);
    }

    /**
     * @return The current aspirants
     */
    public Iterable<String> aspirants() {
        return aspirants;
    }

    /**
     * Check, if the player is member of this team.
     * 
     * @param playerName
     *            The playername
     * @return <code>true</code> if, and only if, the player is member of this
     *         team. Otherwise <code>false</code>
     */
    public boolean isMember(String playerName) {
        return playerName.equals(leaderName) || members.contains(playerName);
    }

    /**
     * @return The current members exclusively the leader name
     */
    public Iterable<String> members() {
        return members;
    }

    /**
     * The leader has accepted the request of the player and converts him to a
     * member of the team. <br>
     * If the player isn't an aspirant, this will have no result
     * 
     * @param currentAspirantName
     *            The players name to become a member
     * @return <code>true</code> if, and only if, the player is at the moment an
     *         aspirant of the team. Otherwise <code>false</code>.
     */
    public boolean convertToMember(String currentAspirantName) {
        if (!this.aspirants.remove(currentAspirantName))
            return false;

        this.members.add(currentAspirantName);
        return true;
    }

    public boolean isPendingMember(String playerName) {
        return this.pendingMembers.contains(playerName);
    }

    public void addToPendingMembers(String offlinePlayerName) {
        this.aspirants.remove(offlinePlayerName);
        this.pendingMembers.add(offlinePlayerName);
        this.members.add(offlinePlayerName);
    }

    public void finishedPendingMember(String playerName) {
        this.pendingMembers.remove(playerName);
    }

    @Override
    public String toString() {
        return "Team [name=" + name + ", startPosition=" + startPosition + ", leaderName=" + leaderName + ", members=" + members + ", aspirants=" + aspirants + ", pendingAccepted=" + pendingMembers + "]";
    }

}

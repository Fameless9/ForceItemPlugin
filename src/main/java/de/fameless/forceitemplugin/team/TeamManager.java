package de.fameless.forceitemplugin.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamManager {
    private static final List<Team> allTeams;
    public static HashMap<Integer, Team> teamMap;

    static {
        allTeams = new ArrayList<Team>();
        TeamManager.teamMap = new HashMap<Integer, Team>();
    }

    public static void createTeam(List<UUID> players) {
        Team newTeam = new Team(TeamManager.allTeams.size() + 1, players);
        TeamManager.allTeams.add(newTeam);
        TeamManager.teamMap.put(TeamManager.allTeams.size(), newTeam);
    }

    public static void deleteTeam(int id) {
        TeamManager.teamMap.get(id).delete();
        TeamManager.teamMap.remove(id);
        TeamManager.allTeams.remove(TeamManager.teamMap.get(id));
    }

    public static Team getTeam(int id) {
        return TeamManager.teamMap.get(id);
    }

    public static Team getTeam(Player player) {
        for (Team team : TeamManager.teamMap.values()) {
            if (team.getPlayers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public static int getTeamAmount() {
        return TeamManager.allTeams.size();
    }

    public static List<Team> getTeamList() {
        return TeamManager.allTeams;
    }
}
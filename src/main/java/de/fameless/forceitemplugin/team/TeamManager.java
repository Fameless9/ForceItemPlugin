package de.fameless.forceitemplugin.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamManager {

    private static final List<Team> allTeams = new ArrayList<>();
    public static HashMap<Integer, Team> teamMap = new HashMap<>();

    public static void createTeam(List<UUID> players) {
        Team newTeam = new Team(allTeams.size() + 1, players);
        allTeams.add(newTeam);
        teamMap.put(allTeams.size(), newTeam);
    }

    public static void deleteTeam(int id) {
         teamMap.get(id).delete();
         teamMap.remove(id);
         allTeams.remove(teamMap.get(id));
    }

    public static Team getTeam(int id) {
        return teamMap.get(id);
    }

    public static Team getTeam(Player player) {
        for (Team team : teamMap.values()) {
            if (team.getPlayers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }
    public static int getTeamAmount() {
        return allTeams.size();
    }
    public static List<Team> getTeamList() {
        return allTeams;
    }
}
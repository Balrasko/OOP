package logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VotingManager {
    private static final Map<String, Integer> votes = new HashMap<>();
    private static final Set<String> hasVotedIDs = new HashSet<>();
    private static final Map<String, Map<String, Integer>> votesByState = new HashMap<>();

    private static int count18to30 = 0;
    private static int count31to60 = 0;
    private static int countOver60 = 0;

    public static boolean hasAlreadyVoted(String ID) {
        return hasVotedIDs.contains(ID);
    }

    public static synchronized boolean addVote(String candidate, String voterName, int age, String state, String ID, int psc) {
        if (hasVotedIDs.contains(ID)) {
            return false;
        }

        state = (state == null || state.trim().isEmpty()) ? "Undetermined" : state;
        votes.put(candidate, votes.getOrDefault(candidate, 0) + 1);
        countVotesByAge(age);
        recordStateVotes(state, candidate);
        hasVotedIDs.add(ID);
        return true;
    }

    private static void countVotesByAge(int age) {
        if (age >= 18 && age <= 30) {
            count18to30++;
        } else if (age > 30 && age <= 60) {
            count31to60++;
        } else if (age > 60) {
            countOver60++;
        }
    }

    private static void recordStateVotes(String state, String candidate) {
        votesByState.computeIfAbsent(state, k -> new HashMap<>()).merge(candidate, 1, Integer::sum);
    }

    public static String getFormattedResults() {
        StringBuilder results = new StringBuilder("<html>");
        votes.forEach((candidate, totalVotes) -> results.append("Candidate: ").append(candidate)
            .append(", Total number of votes: ").append(totalVotes).append("<br>"));

        results.append("Age demographics:<br>")
            .append("18-30: ").append(count18to30).append("<br>")
            .append("31-60: ").append(count31to60).append("<br>")
            .append("60+: ").append(countOver60).append("<br>");

        results.append("Votes by state:<br>");
        votesByState.forEach((state, candidateVotes) -> {
            int totalVotesByState = candidateVotes.values().stream().mapToInt(Integer::intValue).sum();
            results.append("State: ").append(state.isEmpty() ? "Undetermined" : state)
                .append(", Total votes: ").append(totalVotesByState).append("<br>");
        });

        results.append("</html>");
        return results.toString();
    }
}
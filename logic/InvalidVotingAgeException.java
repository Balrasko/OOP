package logic;

/**
 * @author Filip Hromada
 * @version 1.0
 * Definicia vlastnej výnimky InvalidVotingAgeException dediacej od Exception s konštruktorom s parametrom message
 */
public class InvalidVotingAgeException extends Exception {
    public InvalidVotingAgeException(String message) {
        super(message);
    }
}

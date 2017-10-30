package io.leonis.subra.game.data;

import java.io.Serializable;
import java.util.List;
import lombok.Value;

/**
 * The Interface Team.
 *
 * This interface describes the functionality of a team in a Small Size League game.
 *
 * @author Rimon Oz
 */
public interface Team extends Serializable {

  /**
   * @return The {@link TeamColor color} of the team.
   */
  TeamColor getTeamColor();

  /**
   * @return The name of the team.
   */
  String getName();

  /**
   * @return The number of goals scored since the beginning of the game.
   */
  int getScore();

  /**
   * @return The amount of red cards issued to the team.
   */
  int getRedCardCount();

  /**
   * @return The amount of yellow cards issued to the team.
   */
  int getYellowCardCount();

  /**
   * @return Timestamps (in seconds) describing time since epoch when yellow cards were issued.
   */
  List<Integer> getYellowCards();

  /**
   * @return The number of time outs left for this team.
   */
  int getTimeOutsLeft();

  /**
   * @return The time out time left for this team.
   */
  long getTimeOutTimeLeft();

  /**
   * @return The number of the goalie.
   */
  int getGoalieNumber();

  @Value
  class State implements Team {
    private final String name;
    private final int score;
    private final int redCardCount;
    private final int yellowCardCount;
    private final List<Integer> yellowCards;
    private final int timeOutsLeft;
    private final long timeOutTimeLeft;
    private final int goalieNumber;
    private final TeamColor teamColor;
  }
}

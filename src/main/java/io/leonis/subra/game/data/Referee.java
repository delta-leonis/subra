package io.leonis.subra.game.data;

import java.io.Serializable;
import java.util.Set;
import lombok.Value;
import io.leonis.algieba.Temporal;

/**
 * The Class RefereeState.
 *
 * This class represents the state of a Small Size League referee object.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 */
public interface Referee extends Temporal, Serializable {

  /**
   * @return The coarse {@link Stage}.
   */
  Stage getCoarseStage();

  /**
   * @return The amount of the time left in this stage of the game in seconds.
   */
  double getTimeLeftInStage();

  /**
   * @return The latest {@link Command} issued by the referee.
   */
  Command getCommand();

  /**
   * @return The timestamp of the last issued command.
   */
  double getCommandTimeStamp();

  /**
   * @return The number of commands sent since the start of the game.
   */
  double getCommandCount();

  Set<Team> getTeams();

  /**
   * todo insert url of protocol
   */
  enum Command {
    HALT,
    STOP,
    NORMAL_START,
    FORCE_START,
    PREPARE_KICKOFF_YELLOW,
    PREPARE_KICKOFF_BLUE,
    PREPARE_PENALTY_YELLOW,
    PREPARE_PENALTY_BLUE,
    DIRECT_FREE_YELLOW,
    DIRECT_FREE_BLUE,
    INDIRECT_FREE_YELLOW,
    INDIRECT_FREE_BLUE,
    TIMEOUT_YELLOW,
    TIMEOUT_BLUE,
    GOAL_YELLOW,
    GOAL_BLUE,
    UNRECOGNIZED;
  }

  /**
   * todo insert url of protocol
   */
  enum Stage {
    NORMAL_FIRST_HALF_PRE,
    NORMAL_FIRST_HALF,
    NORMAL_HALF_TIME,
    NORMAL_SECOND_HALF_PR,
    NORMAL_SECOND_HALF,
    EXTRA_TIME_BREAK,
    EXTRA_FIRST_HALF_PRE,
    EXTRA_FIRST_HALF,
    EXTRA_HALF_TIME,
    EXTRA_SECOND_HALF_PRE,
    EXTRA_SECOND_HALF,
    PENALTY_SHOOTOUT_BREAK,
    PENALTY_SHOOTOUT,
    POST_GAME,
    UNRECOGNIZED;
  }

  interface Supplier {
    Referee getReferee();
  }

  @Value
  class State implements Referee {
    private final long timestamp;
    private final Stage coarseStage;
    private final double timeLeftInStage;
    private final Command command;
    private final Set<Team> teams;
    private final double commandTimeStamp;
    private final double commandCount;
  }
}

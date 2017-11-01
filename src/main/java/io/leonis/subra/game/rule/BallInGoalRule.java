package io.leonis.subra.game.rule;

import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.Rule;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class BallInGoalRule.
 *
 * This class describes the rule in a Small Size League sequence which
 * determines whether or not a ball is in a goal.
 *
 * @author Rimon Oz
 */
public class BallInGoalRule<I extends Goal.SetSupplier & Ball.SetSupplier>
    implements Rule<I, TeamColor> {

  @Override
  public Set<TeamColor> getViolators(final I input) {
    return input.getBalls().stream()
        .flatMap(ball ->
            input.getGoals().stream()
                .filter(goal -> this.isBallInGoal(ball, goal))
                .map(Goal::getTeamColor))
        .collect(Collectors.toSet());
  }

  /**
   * Determines whether a given {@link Ball} is within the given {@link Goal}.
   *
   * @param ball The {@link Ball} to determine whether or not is within the bounds of the {@link
   *             Goal}.
   * @param goal The {@link Goal} to determine whether or not the {@link Ball} is within its
   *             bounds.
   * @return True if the ball is in the goal, false otherwise.
   */
  public boolean isBallInGoal(final Ball ball, final Goal goal) {
    return StrictMath.abs(ball.getX() - goal.getX())
        < ((goal.getCardinalDirection().equals(CardinalDirection.NORTH)
        || goal.getCardinalDirection().equals(CardinalDirection.SOUTH))
        ? goal.getDepth() / 2f
        : goal.getWidth() / 2f)
        && StrictMath.abs(ball.getY() - goal.getY())
        < ((goal.getCardinalDirection().equals(CardinalDirection.EAST)
        || goal.getCardinalDirection().equals(CardinalDirection.WEST))
        ? goal.getDepth() / 2f
        : goal.getWidth() / 2f);
  }

  @Override
  public boolean test(final I input) {
    return input.getBalls().stream()
        .anyMatch(ball ->
            input.getGoals().stream()
                .anyMatch(goal -> this.isBallInGoal(ball, goal)));
  }
}

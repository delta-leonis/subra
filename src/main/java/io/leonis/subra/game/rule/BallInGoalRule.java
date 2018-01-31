package io.leonis.subra.game.rule;

import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Team.TeamIdentity;
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
public class BallInGoalRule<I extends PositionedGoal.SetSupplier & Ball.SetSupplier>
    implements Rule<I, TeamIdentity> {

  @Override
  public Set<TeamIdentity> getViolators(final I input) {
    return input.getBalls().stream()
        .flatMap(ball ->
            input.getGoals().stream()
                .filter(goal -> this.isBallInGoal(ball, goal))
                .map(PositionedGoal::getTeamIdentity))
        .collect(Collectors.toSet());
  }

  /**
   * Determines whether a given {@link Ball} is within the given {@link GoalDimension}.
   *
   * @param ball The {@link Ball} to determine whether or not is within the bounds of the {@link
   *             GoalDimension}.
   * @param goal The {@link GoalDimension} to determine whether or not the {@link Ball} is within its
   *             bounds.
   * @return True if the ball is in the goal, false otherwise.
   */
  public boolean isBallInGoal(final Ball ball, final PositionedGoal goal) {
    return Math.abs(ball.getX() - goal.getX()) < goal.getDepth() / 2f
        && Math.abs(ball.getY() - goal.getY()) < goal.getWidth() / 2f;
  }

  @Override
  public boolean test(final I input) {
    return input.getBalls().stream()
        .anyMatch(ball ->
            input.getGoals().stream()
                .anyMatch(goal -> this.isBallInGoal(ball, goal)));
  }
}

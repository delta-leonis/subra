package io.leonis.subra.game.rule;

import java.util.Set;
import java.util.stream.Collectors;
import io.leonis.zosma.game.Rule;
import io.leonis.subra.game.data.*;

/**
 * The Class StopRule.
 *
 * This rule determines whether {@link Player robots}are keeping at least 50cm from the ball.
 *
 * @author Rimon Oz
 */
public class StopRule<I extends Player.SetSupplier & Ball.SetSupplier>
    implements Rule<I, Player> {

  @Override
  public Set<Player> getViolators(final I input) {
    return input.getAgents().stream()
        .filter(agent -> this.test(input, agent))
        .collect(Collectors.toSet());
  }

  /**
   * @param ballsSupplier The game state object.
   * @param agent        The {@link Player robot} to verify whether it is at least 50cm from the
   *                     ball.
   * @return True if the {@link Player robot} robot is at least 50cm from the ball, false
   * otherwise.
   */
  public boolean test(final Ball.SetSupplier ballsSupplier, final Player agent) {
    return ballsSupplier.getBalls().stream()
        .anyMatch(ball -> ball.getPosition().distance2(agent.getPosition()) < 500d);
  }

  @Override
  public boolean test(final I input) {
    return input.getAgents().stream()
        .anyMatch(agent -> this.test(input, agent));
  }
}

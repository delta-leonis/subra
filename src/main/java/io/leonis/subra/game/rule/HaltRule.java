package io.leonis.subra.game.rule;

import io.leonis.subra.game.data.Player;
import java.util.Set;
import java.util.stream.Collectors;
import io.leonis.zosma.game.Rule;

/**
 * The Class HaltRule.
 *
 * @author Rimon Oz
 */
public class HaltRule implements Rule<Player.SetSupplier, Player> {

  @Override
  public Set<Player> getViolators(final Player.SetSupplier agentSupplier) {
    return agentSupplier.getAgents().stream()
        .filter(this::test)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean test(final Player.SetSupplier agentSupplier) {
    return agentSupplier.getAgents().stream()
        .anyMatch(this::test);
  }

  /**
   * @param agent The {@link Player} to verify whether it has stopped.
   * @return True if it has stopped, false otherwise.
   */
  public boolean test(final Player agent) {
    return agent.getVelocity().norm2Number().doubleValue() < 0.001d;
  }
}

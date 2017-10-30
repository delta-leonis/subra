package io.leonis.subra.game.rule;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Value;
import io.leonis.zosma.game.Rule;
import io.leonis.subra.game.data.*;

/**
 * The Class TeamSizeRule.
 *
 * @author Rimon Oz
 */
@Value
public class TeamSizeRule implements Rule<Player.SetSupplier, TeamColor> {
  private final int teamSize;

  @Override
  public Set<TeamColor> getViolators(final Player.SetSupplier agentSupplier) {
    return agentSupplier.getAgents().stream()
        .collect(Collectors.groupingBy(Player::getTeamColor))
        .entrySet().stream()
        .filter(entry -> entry.getValue().size() > this.teamSize || entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean test(final Player.SetSupplier agentSupplier) {
    return agentSupplier.getAgents().stream()
        .collect(Collectors.groupingBy(Player::getTeamColor))
        .entrySet().stream()
        .anyMatch(
            entry -> entry.getValue().size() > this.teamSize || entry.getValue().isEmpty());
  }
}

package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.Player;
import io.leonis.zosma.game.engine.Deducer;
import java.util.*;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The Class PlayerVelocityDeducer.
 *
 * This class represents a {@link Deducer} which calculates velocities of {@link Player}.
 *
 * @param <I> The type of state carrying a {@link Set} of {@link Player}.
 * @author Rimon Oz
 */
public class PlayerVelocityDeducer<I extends Player.SetSupplier>
    implements Deducer<I, Set<Player>> {
  @Override
  public Publisher<Set<Player>> apply(final Publisher<I> iPublisher) {
    return Flux.from(iPublisher)
        .scan(
            Collections.emptySet(),
            (previousGame, currentGame) ->
                currentGame.getAgents().stream()
                    .map(currentAgent ->
                        previousGame.stream()
                            .filter(previousAgent ->
                                previousAgent.getId() == currentAgent.getId()
                                    && previousAgent.getTeamColor()
                                    .equals(currentAgent.getTeamColor()))
                            .findFirst()
                            .map(previousAgent ->
                                this.calculateVelocity(currentAgent, previousAgent))
                            .orElse(
                                new Player.State(
                                    currentAgent.getId(),
                                    currentAgent.getTimestamp(),
                                    currentAgent.getX(),
                                    currentAgent.getY(),
                                    currentAgent.getOrientation(),
                                    0d,
                                    0d,
                                    0d,
                                    currentAgent.getTeamColor())))
                    .collect(Collectors.toSet()));
  }

  /**
   * @param currentAgent  The current state of the {@link Player}.
   * @param previousAgent The previous state of the {@link Player}.
   * @return An updated state representation of the current {@link Player} with velocity data.
   */
  private Player calculateVelocity(
      final Player currentAgent,
      final Player previousAgent
  ) {
    return new Player.State(
        currentAgent.getId(),
        currentAgent.getTimestamp(),
        currentAgent.getX(),
        currentAgent.getY(),
        currentAgent.getOrientation(),
        (currentAgent.getX() - previousAgent.getX())
            / (currentAgent.getTimestamp() - previousAgent.getTimestamp()),
        (currentAgent.getY() - previousAgent.getY())
            / (currentAgent.getTimestamp() - previousAgent.getTimestamp()),
        (currentAgent.getOrientation() - previousAgent.getOrientation())
            / (currentAgent.getTimestamp() - previousAgent.getTimestamp()),
        currentAgent.getTeamColor());
  }
}

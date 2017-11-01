package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.*;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The Class PlayersVelocityDeducer.
 *
 * This class represents a {@link Deducer} which calculates velocities of {@link Player}.
 *
 * @param <I> The type of state carrying a {@link Set} of {@link Player}.
 * @author Rimon Oz
 */
public class PlayersVelocityDeducer<I extends Player.SetSupplier>
    implements Deducer<I, Set<MovingPlayer>> {
  @Override
  public Publisher<Set<MovingPlayer>> apply(final Publisher<I> iPublisher) {
    return Flux.from(iPublisher)
        .scan(
            Collections.emptySet(),
            (previousGame, currentGame) ->
                currentGame.getPlayers().stream()
                    .map(currentPlayer ->
                        previousGame.stream()
                            .filter(previousPlayer ->
                                previousPlayer.getIdentity().equals(currentPlayer.getIdentity()))
                            .findFirst()
                            .map(previousPlayer ->
                                this.calculateVelocity(currentPlayer, previousPlayer))
                            .orElse(
                                new MovingPlayer.State(
                                    currentPlayer.getId(),
                                    currentPlayer.getTimestamp(),
                                    currentPlayer.getX(),
                                    currentPlayer.getY(),
                                    currentPlayer.getOrientation(),
                                    0d,
                                    0d,
                                    0d,
                                    currentPlayer.getTeamColor())))
                    .collect(Collectors.toSet()));
  }

  /**
   * @param currentPlayer  The current state of the {@link Player}.
   * @param previousPlayer The previous state of the {@link Player}.
   * @return An updated state representation of the current {@link Player} with velocity data.
   */
  private MovingPlayer calculateVelocity(
      final Player currentPlayer,
      final MovingPlayer previousPlayer
  ) {
    return new MovingPlayer.State(
        currentPlayer.getId(),
        currentPlayer.getTimestamp(),
        currentPlayer.getX(),
        currentPlayer.getY(),
        currentPlayer.getOrientation(),
        (currentPlayer.getX() - previousPlayer.getX())
            / (currentPlayer.getTimestamp() - previousPlayer.getTimestamp()),
        (currentPlayer.getY() - previousPlayer.getY())
            / (currentPlayer.getTimestamp() - previousPlayer.getTimestamp()),
        (currentPlayer.getOrientation() - previousPlayer.getOrientation())
            / (currentPlayer.getTimestamp() - previousPlayer.getTimestamp()),
        currentPlayer.getTeamColor());
  }
}

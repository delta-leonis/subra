package io.leonis.subra.game.engine;

import io.leonis.zosma.game.engine.Deducer;
import java.util.*;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import io.leonis.subra.game.data.Ball;
import reactor.core.publisher.Flux;

/**
 * The Class BallsVelocityDeducer.
 *
 * This class represents a {@link Deducer} which calculates velocities of {@link Ball}.
 *
 * @param <I> The type of state carrying a {@link Set} of {@link Ball}.
 * @author Rimon Oz
 */
public class BallsVelocityDeducer<I extends Ball.SetSupplier> implements Deducer<I, Set<Ball>> {
  @Override
  public Publisher<Set<Ball>> apply(final Publisher<I> iPublisher) {
    return Flux.from(iPublisher)
        .scan(Collections.emptySet(), (previousGame, currentGame) ->
            currentGame.getBalls().stream()
                .map(currentBall ->
                    previousGame.stream()
                        .reduce((closerBall, newBall) ->
                            newBall.getPosition()
                                .sub(currentBall.getPosition()).norm2Number().doubleValue()
                                > closerBall.getPosition()
                                .sub(currentBall.getPosition()).norm2Number().doubleValue()
                                ? newBall : closerBall)
                        .map(closestBall ->
                            this.calculateVelocity(currentBall, closestBall))
                        .orElse(
                            new Ball.State(
                                currentBall.getTimestamp(),
                                currentBall.getX(),
                                currentBall.getY(),
                                currentBall.getZ(),
                                0d,
                                0d,
                                0d)))
                .collect(Collectors.toSet()));
  }

  /**
   * @param currentBall  The current state of the {@link Ball}.
   * @param previousBall The previous state of the {@link Ball}.
   * @return An updated state representation of the current {@link Ball} with velocity data.
   */
  private Ball calculateVelocity(final Ball currentBall, final Ball previousBall) {
    return new Ball.State(
        currentBall.getTimestamp(),
        currentBall.getX(),
        currentBall.getY(),
        currentBall.getZ(),
        (currentBall.getX() - previousBall.getX())
            / (currentBall.getTimestamp() - previousBall.getTimestamp()),
        (currentBall.getY() - previousBall.getY())
            / (currentBall.getTimestamp() - previousBall.getTimestamp()),
        (currentBall.getZ() - previousBall.getZ())
            / (currentBall.getTimestamp() - previousBall.getTimestamp()));
  }
}

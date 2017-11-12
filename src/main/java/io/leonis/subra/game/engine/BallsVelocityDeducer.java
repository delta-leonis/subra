package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.*;
import java.util.stream.Collectors;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The Class BallsVelocityDeducer.
 *
 * This class represents a {@link Deducer} which calculates velocities of {@link Ball}.
 *
 * @param <I> The type of state carrying a {@link Set} of {@link Ball}.
 * @author Rimon Oz
 */
public class BallsVelocityDeducer<I extends Ball.SetSupplier>
    implements Deducer<I, Set<MovingBall>> {
  @Override
  public Publisher<Set<MovingBall>> apply(final Publisher<I> iPublisher) {
    return Flux.from(iPublisher)
        .scan(Collections.emptySet(), (previousGame, currentGame) ->
            currentGame.getBalls().stream()
                .map(currentBall ->
                    previousGame.stream()
                        .reduce((closerBall, newBall) ->
                            Transforms.euclideanDistance(newBall.getXY(), currentBall.getXY())
                                > Transforms.euclideanDistance(
                                closerBall.getXY(), currentBall.getXY())
                                ? newBall
                                : closerBall)
                        .map(closestBall -> this.calculateVelocity(currentBall, closestBall))
                        .orElse(
                            new MovingBall.State(
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
  private MovingBall calculateVelocity(final Ball currentBall, final MovingBall previousBall) {
    return new MovingBall.State(
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

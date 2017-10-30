package io.leonis.subra.game.engine;

import com.google.common.collect.Lists;
import io.leonis.subra.game.data.Strategy;
import io.leonis.subra.game.data.Strategy.Supplier;
import io.leonis.subra.math.PlayerCommandGroup;
import io.leonis.zosma.game.engine.Deducer;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.reactivestreams.Publisher;
import io.leonis.zosma.game.*;
import io.leonis.subra.game.data.*;
import io.leonis.algieba.*;
import io.leonis.algieba.control.PSDController;
import io.leonis.algieba.geometry.Rotation;
import reactor.core.publisher.Flux;
import reactor.util.function.*;

/**
 * The Class PSDFormationDeducer.
 *
 * This class represents an {@link Deducer} which controls {@link Player robot} positions through
 * {@link PSDController} on all three axes.
 *
 * @author Rimon Oz
 */
@Value
public class PSDFormationDeducer<F extends Player.SetSupplier & Temporal & Formation.Supplier<Formation<INDArray, Player>>>
    implements PlayerCommandGroup, Rotation, Deducer<F, Supplier> {

  private final double proportionalFactorX;
  private final double summationFactorX;
  private final double differenceFactorX;
  private final double proportionalFactorY;
  private final double summationFactorY;
  private final double differenceFactorY;
  private final double proportionalFactorZ;
  private final double summationFactorZ;
  private final double differenceFactorZ;

  /**
   * @param gameBuffer A {@link List} of {@link Tuple2} containing a game state with the corresponding
   *                   formation, and a mapping from {@link Player} to a vector representing the
   *                   difference between the agent's current position and the desired position.
   * @return The {@link Strategy.Supplier strategy} which minimizes the difference between {@link
   * Player agent} positions and their {@link Formation} positions.
   */
  public Strategy.Supplier strategize(
      final List<Tuple2<F, Map<Player, INDArray>>> gameBuffer
  ) {
    return () ->
        gameBuffer.get(0).getT1().getAgents().stream()
            .collect(Collectors.toMap(
                player -> player,
                player -> new PlayerCommand.State(
                    Nd4j.vstack(this.planarCartesian(Nd4j.vstack(
                        this.computeCoordinateMagnitude(gameBuffer, player, 0),
                        this.computeCoordinateMagnitude(gameBuffer, player, 1)),
                        -1 * player.getOrientation()),
                        this.computeCoordinateMagnitude(gameBuffer, player, 2)),
                    0,
                    0,
                    0)));
  }

  /**
   * @param gameBuffer      A {@link List} of {@link Tuple2} containing a game state with the
   *                        corresponding formation, and a mapping from {@link Player} to a vector
   *                        representing the difference between the agent's current position and the
   *                        desired position.
   * @param player          The {@link Player} to compute the coordinate magnitude for.
   * @param coordinateIndex The index of the coordinate (0 = x, 1 = y, 2 = orientation).
   * @return The coordinate magnitude as an INDArray.
   */
  private INDArray computeCoordinateMagnitude(
      final List<Tuple2<F, Map<Player, INDArray>>> gameBuffer,
      final Player player,
      final int coordinateIndex
  ) {
    return PSDController.apply(
        gameBuffer.get(0).getT2().get(player)
            .get(NDArrayIndex.interval(coordinateIndex, coordinateIndex + 1), NDArrayIndex.all()),
        gameBuffer.stream()
            .map(Tuple2::getT2)
            .map(map -> map.get(player))
            .map(error -> error
                .get(NDArrayIndex.interval(coordinateIndex, coordinateIndex + 1),
                    NDArrayIndex.all()))
            .collect(Collectors.toList()),
        this.proportionalFactorZ,
        this.summationFactorZ,
        this.differenceFactorZ,
        (double) (gameBuffer.get(0).getT1().getTimestamp()
            - gameBuffer.get(1).getT1().getTimestamp()));
  }

  @Override
  public Publisher<Strategy.Supplier> apply(
      final Publisher<F> inputPublisher
  ) {
    return Flux.from(inputPublisher)
        .buffer(2, 1)
        .map(Lists::reverse)
        .map(this::computeError)
        .buffer(3, 1)
        .map(Lists::reverse)
        .map(this::strategize);
  }

  /**
   * @param gameBuffer A {@link List sequential history} of {@link Tuple2} containing game states
   *                   and their corresponding {@link Formation}.
   * @return A {@link List} of {@link Tuple3} containing a game state, the corresponding formation,
   * and a mapping from {@link Player} to a vector representing the difference between the agent's
   * current position and the desired position.
   */
  private Tuple2<F, Map<Player, INDArray>> computeError(
      final List<F> gameBuffer
  ) {
    return Tuples.of(
        gameBuffer.get(0),
        gameBuffer.get(0).getAgents().stream()
            .collect(Collectors.toMap(
                player -> player,
                player ->
                    gameBuffer.get(0).getFormation().getFormationFor(player)
                        .sub(Nd4j.vstack(
                            player.getPosition(),
                            Nd4j.create(new double[]{player.getOrientation()}))))));
  }
}

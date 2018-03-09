package io.leonis.subra.game.engine.psd;

import io.leonis.algieba.control.PSDController;
import io.leonis.algieba.geometry.Vectors;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.game.data.Strategy.Supplier;
import io.leonis.subra.game.formations.Formation;
import io.reactivex.functions.Function;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import reactor.util.function.*;

/**
 * @author Rimon Oz
 */
@AllArgsConstructor
public final class FormationStragetizer implements
    Function<List<Tuple3<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>, Map<PlayerIdentity, Tuple2<Player, INDArray>>>>, Supplier> {
  private final double proportionalFactorZ;
  private final double summationFactorZ;
  private final double differenceFactorZ;

  /**
   * @param gameBuffer A {@link List} of {@link Tuple2} containing a game state with the
   *                   corresponding formation, and a mapping from {@link Player} to a vector
   *                   representing the difference between the agent's current position and the
   *                   desired position.
   * @return The {@link Strategy.Supplier strategy} which minimizes the difference between {@link
   * Player agent} positions and their {@link Formation} positions.
   */
  @Override
  public Supplier apply(
      final List<Tuple3<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>, Map<PlayerIdentity, Tuple2<Player, INDArray>>>> gameBuffer) {
    return () ->
        gameBuffer.get(0).getT1().stream()
            .collect(Collectors.toMap(
                Player::getIdentity,
                player -> new PlayerCommand.State(
                    Nd4j.vstack(Vectors.rotatePlanarCartesian(Nd4j.vstack(
                        this.computeCoordinateMagnitude(gameBuffer, player, 0),
                        this.computeCoordinateMagnitude(gameBuffer, player, 1)),
                        -1 * player.getOrientation()),
                        this.computeCoordinateMagnitude(gameBuffer, player, 2)),
                    0,
                    0,
                    0)));
  }

  /**
   * @param gameBuffer      A {@link List} of {@link Tuple3} containing a game state with the
   *                        corresponding formation, and a mapping from {@link Player} to a vector
   *                        representing the difference between the agent's current position and the
   *                        desired position.
   * @param player          The {@link Player} to compute the coordinate magnitude for.
   * @param coordinateIndex The index of the coordinate (0 = x, 1 = y, 2 = orientation).
   * @return The coordinate magnitude as an INDArray.
   */
  private INDArray computeCoordinateMagnitude(
      final List<Tuple3<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>, Map<PlayerIdentity, Tuple2<Player, INDArray>>>> gameBuffer,
      final Player player,
      final int coordinateIndex
  ) {
    return PSDController.apply(
        gameBuffer.get(0).getT3().get(player.getIdentity()).getT2()
            .get(NDArrayIndex.interval(coordinateIndex, coordinateIndex + 1), NDArrayIndex.all()),
        gameBuffer.stream()
            .map(Tuple3::getT3)
            .map(map -> map.get(player.getIdentity()))
            .map(error -> error.getT2()
                .get(NDArrayIndex.interval(coordinateIndex, coordinateIndex + 1),
                    NDArrayIndex.all()))
            .collect(Collectors.toList()),
        this.proportionalFactorZ,
        this.summationFactorZ,
        this.differenceFactorZ,
        (double) (gameBuffer.get(0).getT3().get(player.getIdentity()).getT1().getTimestamp()
            - gameBuffer.get(1).getT3().get(player.getIdentity()).getT1().getTimestamp()));
  }
}

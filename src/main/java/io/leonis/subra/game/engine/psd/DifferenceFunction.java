package io.leonis.subra.game.engine.psd;

import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.game.formations.Formation;
import io.reactivex.functions.Function;
import java.util.*;
import java.util.stream.Collectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import reactor.util.function.*;

/**
 * TODO verify implementation. Only first item of provided list is used.
 * TODO maybe ombeunen to BiFunction<Set<Player>, Formation<>, TupleThing>.
 * @author Rimon Oz
 */
public class DifferenceFunction implements Function<List<Tuple2<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>>>,
    Tuple2<Tuple2<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>>, Map<PlayerIdentity, Tuple2<Player, INDArray>>>> {

  /**
   * @param gameBuffer A {@link List sequential history} of {@link Tuple2} containing game states
   *                   and their corresponding {@link Formation}.
   * @return A {@link List} of {@link Tuple3} containing a game state, the corresponding formation,
   * and a mapping from {@link Player} to a vector representing the difference between the agent's
   * current position and the desired position.
   */
  @Override
  public Tuple2<Tuple2<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>>, Map<PlayerIdentity, Tuple2<Player, INDArray>>> apply(
      final List<Tuple2<Set<MovingPlayer>, Formation<PlayerIdentity, INDArray>>> gameBuffer)
      throws Exception {
    return Tuples.of(
        gameBuffer.get(0),
        gameBuffer.get(0).getT1().stream()
            .collect(Collectors.toMap(
                Player::getIdentity,
                player -> Tuples.of(
                    player,
                    gameBuffer.get(0).getT2().getFormationFor(player.getIdentity())
                        .sub(player.getPosition())))));
  }
}

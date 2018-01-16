package io.leonis.subra.math;

import io.leonis.subra.game.data.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.stream.*;
import lombok.experimental.Delegate;

/**
 * The Class CombineByPlayerStrategySupplier.
 *
 * This class represents the computation of combining two {@link Strategy.Supplier} by
 * {@link io.leonis.subra.game.data.Player.PlayerIdentity}.
 *
 * @author Rimon Oz
 */
public class CombineByPlayerStrategySupplier implements Strategy.Supplier {
  @Delegate
  private final Strategy.Supplier strategy;

  /**
   * Computes the {@link Strategy.Supplier} as a result of combining the supplied strategies by
   * {@link io.leonis.subra.game.data.Player.PlayerIdentity}.
   *
   * @param leftMap  The first strategy to combine.
   * @param rightMap The second strategy to combine.
   * @param combiner The {@link BinaryOperator combinator} function.
   */
  public CombineByPlayerStrategySupplier(
      final BinaryOperator<PlayerCommand> combiner,
      final Strategy.Supplier leftMap,
      final Strategy.Supplier... rightMaps
  ) {
    this.strategy =
        Stream.of(rightMaps)
            .reduce(leftMap,
                (left, right) ->
                    () -> left.getStrategy().entrySet().stream()
                        .collect(Collectors.toMap(
                            Entry::getKey,
                            entry ->
                                right.getStrategy().containsKey(entry.getKey())
                                    ? combiner.apply(right.getStrategy().get(entry.getKey()),
                                    entry.getValue())
                                    : entry.getValue())));
  }
}

package io.leonis.subra.gui;

import io.leonis.subra.game.data.Player;
import java.util.*;
import java.util.stream.Collectors;
import org.nd4j.linalg.api.iter.NdIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * todo remove
 */
public class FieldGenerator {

  public static INDArray whatever(final Set<Player> players) {
    return createArray(100, 30, 3, 3,
        players.stream()
            .map(player -> Nd4j.create(new double[]{player.getX(), player.getY()}, new int[]{2, 1}))
            .collect(Collectors.toSet())).get();
  }

  public static Optional<INDArray> createArray(
      final int rows,
      final int columns,
      final double height,
      final double spread,
      final Set<INDArray> origins
  ) {
    if (origins.stream().allMatch(origin -> Arrays.equals(origin.shape(), new int[]{2, 1}))
        && (rows > 0) && (columns) > 0) {
      return origins.stream()
          .map(origin -> {
            INDArray partialField = Nd4j.create(rows, columns);
            NdIndexIterator iterator = new NdIndexIterator(rows, columns);

            while (iterator.hasNext()) {
              final int[] coordinates = iterator.next();

              partialField.putScalar(
                  coordinates,
                  height * StrictMath.exp(-1d * StrictMath.pow((1d / spread) * Nd4j.create(
                      Arrays.stream(coordinates).asDoubleStream().toArray(),
                      new int[]{2, 1}).sub(origin).norm2Number().doubleValue(), 2)));
            }
            return partialField;
          })
          .reduce(INDArray::add);
    } else {
      return Optional.empty();
    }
  }
}
package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TextColor;
import io.leonis.algieba.spatial.PotentialField;
import java.util.function.*;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * The class GaussianPotentialFieldBackground.
 *
 * Provides a {@link TextColor} based on a provided {@link Function gradient} and the potential at
 * a specific x and y.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class PotentialFieldBackground implements
    BiFunction<Integer, Integer, TextColor> {

  /**
   * Value considered absolute max of any value of the potentialField
   */
  private final static double LIMIT = 100;
  /**
   * The GaussianPotentialField as source for the gradient
   */
  private final PotentialField potentialField;
  /**
   * Gradient to determine color based on ratio
   */
  private final Function<Double, TextColor.RGB> gradient;

  @Override
  public TextColor apply(final Integer x, final Integer y) {
    final INDArray potential = potentialField
        .getPotential(Nd4j.create(new double[]{x, y}, new int[]{2, 1}));
    return gradient.apply(Math.min(potential.getDouble(0, 0) / LIMIT, 1));
  }
}

package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TextColor;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import java.util.function.*;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class GaussianPotentialFieldBackground implements BiFunction<Integer, Integer, TextColor> {

  private final static double LIMIT = 100;
  private final GaussianPotentialField potentialField;
  private final Function<Double, TextColor.RGB> gradient;

  @Override
  public TextColor apply(final Integer x, final Integer y) {
    final INDArray positionVector = potentialField.getPotential(Nd4j.create(new double[]{x, y}));
    return gradient.apply(positionVector.getDouble(0, 0) / LIMIT);
  }
}

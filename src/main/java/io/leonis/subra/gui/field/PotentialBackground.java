package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TextColor;
import java.util.function.*;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author Jeroen de Jong
 */
public class PotentialBackground implements BiFunction<Integer, Integer, TextColor> {
  private final Function<Double, TextColor.RGB> gradient;
  private final INDArray data;
  private final double max;

  public PotentialBackground(final INDArray data, final Function<Double, TextColor.RGB> gradient) {
    this.data = data;
    this.gradient = gradient;
    this.max = data.maxNumber().doubleValue();
  }

  @Override
  public TextColor apply(final Integer x, final Integer y){
    return gradient.apply(data.getDouble(y, x) / max);
  }
}

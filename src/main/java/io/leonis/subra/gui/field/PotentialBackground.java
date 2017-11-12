package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import java.util.function.Function;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author Jeroen de Jong
 */
public class PotentialBackground implements Function<TerminalPosition, TextColor> {
  private final Function<Double, TextColor.RGB> gradient;
  private final INDArray data;
  private final double max;

  public PotentialBackground(final INDArray data, final Function<Double, TextColor.RGB> gradient) {
    this.data = data;
    this.gradient = gradient;
    this.max = data.maxNumber().doubleValue();
  }

  @Override
  public TextColor apply(final TerminalPosition position) {
    return this.gradient.apply(this.data.getDouble(position.getColumn(), position.getRow()) / this.max);
  }
}

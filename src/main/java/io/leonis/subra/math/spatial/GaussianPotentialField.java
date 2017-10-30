package io.leonis.subra.math.spatial;

import io.leonis.algieba.geometry.Rotation;
import io.leonis.algieba.spatial.PotentialField;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * The Class GaussianPotentialField.
 *
 * This class represents a {@link PotentialField} of an object which attracts other objects.
 *
 * @author Rimon Oz
 */
@Value
public class GaussianPotentialField implements PotentialField, Rotation {
  private final INDArray origin;
  private final double scale;
  private final boolean sink;

  @Override
  public INDArray getPotential(final INDArray positionVector) {
    return Nd4j.create(new double[]{
        StrictMath
            .exp(-1d * StrictMath
            .pow(scale * this.origin.add(positionVector).norm2Number().doubleValue(), 2))
    });
  }

  @Override
  public INDArray getForce(final INDArray positionVector) {
    return this.planarCartesian(
        // vector amplitude
        Nd4j.zeros(positionVector.rows(), 1)
            .put(0, 0,
                (sink ? -1 : 1) *
                    (-2d * this.origin.add(positionVector).norm2Number().doubleValue()
                        * StrictMath.pow(this.scale, 2)
                        * StrictMath
                        .exp(-1d * StrictMath
                            .pow(
                                scale * this.origin.add(positionVector).norm2Number().doubleValue(),
                                2)))),
        // vector angle
        positionVector.sub(this.origin)
            .mmul(positionVector.sub(this.origin).transpose())
            .div(Math.pow(positionVector.sub(this.origin).norm2Number().doubleValue(), 2))
            .getDouble(0, 0));
  }
}

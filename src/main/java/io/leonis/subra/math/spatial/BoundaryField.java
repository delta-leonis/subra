package io.leonis.subra.math.spatial;

import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.algieba.spatial.PotentialField;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * The Class BoundaryField.
 *
 * This class represents a {@link PotentialField} of a {@link io.leonis.subra.game.data.Field} such
 * that the potential approximates zero for any point on the field and approximates infinity for any
 * point outside the field.
 *
 * @author Rimon Oz
 */
@Value
public class BoundaryField implements PotentialField {
  private final INDArray origin = Nd4j.zeros(2, 1);
  /**
   * The width of the field in mm.
   */
  private final double width;
  /**
   * The length of the field in mm.
   */
  private final double length;
  /**
   * The displacement of the potential field from the edge.
   */
  private final double fieldDisplacement;

  @Override
  public INDArray getPotential(final INDArray positionVector) {
    return Nd4j.create(new double[]{
        this.getPotential(positionVector.getDouble(0, 0))
            + this.getPotential(this.getWidth() - positionVector.getDouble(0, 0))
            + this.getPotential(positionVector.getDouble(1, 0))
            + this.getPotential(this.getLength() - positionVector.getDouble(1, 0))});

  }

  public double getPotential(final double distanceToBoundary) {
    return 1 / (1 + Math.exp(this.getFieldDisplacement() - distanceToBoundary));
  }

  @Override
  public INDArray getForce(final INDArray positionVector) {
    return this.getForce(positionVector, CardinalDirection.NORTH)
        .add(this.getForce(positionVector, CardinalDirection.SOUTH))
        .add(this.getForce(positionVector, CardinalDirection.EAST))
        .add(this.getForce(positionVector, CardinalDirection.WEST));
  }

  public INDArray getForce(final INDArray positionVector, final CardinalDirection direction) {
    switch (direction) {
      case NORTH:
        return Nd4j.create(new double[]{
            0,
            this.getPotential(positionVector.getDouble(0, 0)),
        }, new int[]{2, 1});
      case SOUTH:
        return Nd4j.create(new double[]{
            0,
            this.getPotential(this.getWidth() - positionVector.getDouble(0, 0)),
        }, new int[]{2, 1});
      case EAST:
        return Nd4j.create(new double[]{
            this.getPotential(positionVector.getDouble(1, 0)),
            0
        }, new int[]{2, 1});
      case WEST:
        return Nd4j.create(new double[]{
            this.getPotential(this.getLength() - positionVector.getDouble(1, 0)),
            0
        }, new int[]{2, 1});
      default:
        return Nd4j.zeros(2, 1);
    }
  }
}

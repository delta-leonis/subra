package io.leonis.subra.math.spatial;

import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.algieba.spatial.PotentialField;
import java.util.function.UnaryOperator;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

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

  /**
   * {@inheritDoc}
   */
  @Override
  public INDArray getPotential(final INDArray positionVector) {
    return Nd4j.create(new double[]{
        this.getPotential(positionVector.getDouble(0, 0))
            + this.getPotential(this.getWidth() - positionVector.getDouble(0, 0))
            + this.getPotential(positionVector.getDouble(1, 0))
            + this.getPotential(this.getLength() - positionVector.getDouble(1, 0))});
  }

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=1%2F(1+%2B+e%5E(D+-+d))">this equation.</a>
   *
   * @param distanceToBoundary The distance to the boundary in mm.
   * @return The potential due to a single boundary at the specified distance from that boundary.
   */
  public double getPotential(final double distanceToBoundary) {
    return 1 / (1 + Math.exp(this.getFieldDisplacement() - distanceToBoundary));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public INDArray getForce(final INDArray positionVector) {
    return this.getForce(positionVector, CardinalDirection.NORTH)
        .add(this.getForce(positionVector, CardinalDirection.SOUTH))
        .add(this.getForce(positionVector, CardinalDirection.EAST))
        .add(this.getForce(positionVector, CardinalDirection.WEST));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public INDArray getAccumulate(final INDArray origin, final INDArray target) {
    return Nd4j.create(
        new double[]{
            Math.sqrt(Transforms.pow(target.sub(origin), 2).sumNumber().doubleValue())
                * (this.computeLineIntegral(origin, target, CardinalDirection.NORTH).apply(1d)
                + this.computeLineIntegral(origin, target, CardinalDirection.SOUTH).apply(1d)
                + this.computeLineIntegral(origin, target, CardinalDirection.EAST).apply(1d)
                + this.computeLineIntegral(origin, target, CardinalDirection.WEST).apply(1d)
                - this.computeLineIntegral(origin, target, CardinalDirection.NORTH).apply(0d)
                - this.computeLineIntegral(origin, target, CardinalDirection.SOUTH).apply(0d)
                - this.computeLineIntegral(origin, target, CardinalDirection.EAST).apply(0d)
                - this.computeLineIntegral(origin, target, CardinalDirection.WEST).apply(0d)
            )});
  }

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=integral+of+1%2F(1+%2B+e%5E(D+-+(A*t+%2B+B)))dt">
   * this equation</a>.
   *
   * @param origin    The starting point of the line integral.
   * @param target    The target point of the line integral
   * @param direction The {@link CardinalDirection} corresponding to the location of the boundary.
   * @return The total potential between the supplied origin and target due to the boundary located
   * in the supplied direction.
   */
  public UnaryOperator<Double> computeLineIntegral(final INDArray origin, final INDArray target,
      final CardinalDirection direction) {
    switch (direction) {
      case NORTH:
        return this.computeLineIntegral(origin.getDouble(0, 0), target.getDouble(0, 0));
      case SOUTH:
        return this.computeLineIntegral(this.getWidth() - origin.getDouble(0, 0),
            this.getWidth() - target.getDouble(0, 0));
      case EAST:
        return this.computeLineIntegral(origin.getDouble(1, 0), target.getDouble(1, 0));
      case WEST:
        return this.computeLineIntegral(this.getLength() - origin.getDouble(1, 0),
            this.getLength() - target.getDouble(1, 0));
      default:
        return input -> 0d;
    }
  }

  /**
   * @param origin The starting distance from the boundary.
   * @param target The target distance from the boundary.
   * @return The total potential between the supplied origin and target distances from a boundary.
   */
  public UnaryOperator<Double> computeLineIntegral(final double origin, final double target) {
    return input -> {
      if (origin == target) {
        return 0d;
      } else {
        return Math.log(
            Math.exp((target - origin) * input + origin) + Math.exp(this.getFieldDisplacement()))
            / (target - origin);
      }
    };
  }

  /**
   * @param positionVector The position at which to compute the gradient of the potential.
   * @param direction      The {@link CardinalDirection} matching the boundary for which to compute
   *                       the gradient of the potential.
   * @return The gradient of the potential at the specified point due to the boundary located in the
   * specified {@link CardinalDirection}.
   */
  public INDArray getForce(final INDArray positionVector, final CardinalDirection direction) {
    switch (direction) {
      case NORTH:
        return Nd4j.create(new double[]{
            -1 * this.getForceMagnitude(positionVector.getDouble(0, 0)),
            0,
        }, new int[]{2, 1});
      case SOUTH:
        return Nd4j.create(new double[]{
            this.getForceMagnitude(this.getWidth() - positionVector.getDouble(0, 0)),
            0,
        }, new int[]{2, 1});
      case EAST:
        return Nd4j.create(new double[]{
            0,
            -1 * this.getForceMagnitude(positionVector.getDouble(1, 0))
        }, new int[]{2, 1});
      case WEST:
        return Nd4j.create(new double[]{
            0,
            this.getForceMagnitude(this.getLength() - positionVector.getDouble(1, 0))
        }, new int[]{2, 1});
      default:
        return Nd4j.zeros(2, 1);
    }
  }

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=derivative+of+1%2F(1+%2B+e%5E(D+-+d))">this
   * equation</a>.
   *
   * @param distanceToBoundary The distance from the boundary for which the compute the magnitude of
   *                           the force vector.
   * @return The magnitude of the force vector due to a single boundary.
   */
  public double getForceMagnitude(final double distanceToBoundary) {
    return Math.exp(distanceToBoundary + this.getFieldDisplacement())
        / Math.pow(Math.exp(distanceToBoundary) + Math.exp(this.getFieldDisplacement()), 2);
  }
}

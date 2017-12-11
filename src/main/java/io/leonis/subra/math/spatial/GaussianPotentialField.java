package io.leonis.subra.math.spatial;

import io.leonis.algieba.geometry.Rotation;
import io.leonis.algieba.spatial.PotentialField;
import io.leonis.algieba.statistic.distribution.GaussianDistribution;
import java.util.function.UnaryOperator;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * The Class GaussianPotentialField.
 *
 * This class represents a {@link PotentialField} of an object which attracts other objects.
 *
 * @author Rimon Oz
 */
@Value
public class GaussianPotentialField implements PotentialField, Rotation {
  /**
   * A vector pointing to the origin.
   */
  private final INDArray origin;
  /**
   * Height in potential.
   */
  private final double height;
  /**
   * Width of the field.
   */
  private final double width;
  /**
   * Length of the field.
   */
  private final double length;
  /**
   * Angle in radians.
   */
  private final double angle;
  /**
   * Whether this field should act as a sink or source.
   */
  private final boolean sink;

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=H+*+e%5E(-1*(x%5E2%2FW%2By%5E2%2FL))"> this
   * equation</a>.
   *
   * @param positionVector A position vector for which to calculate the potential.
   * @return The potential at the supplied position vector.
   */
  @Override
  public INDArray getPotential(final INDArray positionVector) {
    return Nd4j.create(new double[]{
        Transforms.exp(
            Transforms.pow(this.toLocalSpace(positionVector), 2).mul(-1d))
            .prodNumber().doubleValue()
            * ((this.sink ? -1 : 1) * this.height)});
  }

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=derivative+of+H+*+e%5E(-1*(x%5E2%2FW%2By%5E2%2FL))">
   * this equation</a>.
   *
   * @param positionVector A position vector for which to compute the force vector.
   * @return The force at the supplied position vector due to the potential field.
   */
  @Override
  public INDArray getForce(final INDArray positionVector) {
    return this.toGlobalSpace(this.toLocalSpace(positionVector)
        .mul(this.getPotential(positionVector).getDouble(0, 0))
        .mul(this.sink ? -2 : 2)
        .div(Nd4j.create(new double[]{this.getWidth(), this.getLength()}, new int[]{2, 1})));
  }

  /**
   * See <a href="http://www.wolframalpha.com/input/?i=integral+of+H+*+e%5E(-1*((A*t%2BB)%5E2%2FW%2B(C*t%2BD)%5E2%2FL))dt">
   * this equation</a>, where <code>x = A*t + B</code> and <code>y = C*t + D</code>.
   *
   * @param origin The starting point of the line along which to calculate the total potential.
   * @param target The final point of the line along which to calculate the total potential.
   * @return The total, or accumulated, potential along the line between the supplied position
   * vectors.
   */
  @Override
  public INDArray getAccumulate(final INDArray origin, final INDArray target) {
    // translate to local space
    final INDArray newOrigin = this.toLocalSpace(origin);
    final INDArray newTarget = this.toLocalSpace(target);

    // compute parametric coefficients
    final double A = newTarget.getDouble(0, 0) - newOrigin.getDouble(0, 0);
    final double C = newTarget.getDouble(1, 0) - newOrigin.getDouble(1, 0);

    // compute indefinite integral
    final UnaryOperator<Double> integral = this.computeLineIntegral(newOrigin, newTarget);

    // compute parametric scaling parameter
    final double l = Math.sqrt(
        Transforms.pow(Nd4j.create(new double[]{A, C}, new int[]{2, 1}), 2).sumNumber()
            .doubleValue());

    // compute line integral
    return Nd4j.create(new double[]{l * (integral.apply(1d) - integral.apply(0d))});
  }

  /**
   * @param origin The starting point of the line along which to calculate the unscaled value of the
   *               parametrized line integral.
   * @param target The final point of the line along which to calculate the unscaled value of the
   *               parametrized line integral.
   * @return A {@link UnaryOperator} representing the indefinite integral of the parametrized line
   * integral.
   */
  public UnaryOperator<Double> computeLineIntegral(final INDArray origin, final INDArray target) {
    return input -> {
      // compute parametric coefficients
      final double A = target.getDouble(0, 0) - origin.getDouble(0, 0);
      final double B = origin.getDouble(0, 0);
      final double C = target.getDouble(1, 0) - origin.getDouble(1, 0);
      final double D = origin.getDouble(1, 0);

      // compute leading coefficients
      final double coeff = this.height * Math.sqrt(Math.PI * this.getLength() * this.getWidth())
          / (2 * Math.sqrt(Math.pow(A, 2) * this.getLength() + Math.pow(C, 2) * this.getWidth()));

      // compute exponential expression
      final double eTerm = Math.exp(
          -1 * Math.pow(B * C - A * D, 2)
              / (Math.pow(A, 2) * this.getLength() + Math.pow(C, 2) * this.getWidth()));

      // compute erf expression
      final double erf = GaussianDistribution.erf(
          Math.pow(A, 2) * this.getLength() * input
              + Math.pow(C, 2) * this.getWidth() * input
              + A * B * this.getLength()
              + C * D * this.getWidth());

      // compute unscaled line integral
      return coeff * eTerm * erf;
    };
  }

  /**
   * @param positionVector The position vector which to change basis for.
   * @return The supplied (global) position vector expressed in terms of the basis formed by the
   * potential field.
   */
  private INDArray toLocalSpace(final INDArray positionVector) {
    return this.planarCartesian(positionVector.sub(this.getOrigin()), this.getAngle())
        .div(Nd4j.create(new double[]{this.getLength(), this.getWidth()}, new int[]{2, 1}));
  }

  /**
   * @param positionVector The position vector which to change basis for.
   * @return The supplied (local) position vector expressed in terms of the standard basis.
   */
  private INDArray toGlobalSpace(final INDArray positionVector) {
    return this.planarCartesian(
        positionVector
            .mul(Nd4j.create(new double[]{this.getLength(), this.getWidth()}, new int[]{2, 1})),
        -1 * this.getAngle());
  }
}

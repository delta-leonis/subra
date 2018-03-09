package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.algieba.geometry.Vectors;
import io.leonis.subra.game.data.GoalDimension;
import io.reactivex.functions.Function;
import org.robocup.ssl.Geometry.GeometryData;

/**
 * @author jeroen.dejong.
 */
public class GoalDimensionSelector implements Function<GeometryData, GoalDimension> {

  @Override
  public GoalDimension apply(final GeometryData geometryData) {
    return new GoalDimension.State(
        Vectors.columnVector(
            geometryData.getField().getGoalWidth(),
            geometryData.getField().getGoalDepth()));
  }
}

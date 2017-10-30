package io.leonis.subra.math.filter;

import io.leonis.algieba.filter.KalmanFilter;
import io.leonis.algieba.statistic.SimpleDistribution;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.reactivestreams.Publisher;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Goal.SetSupplier;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.algieba.Temporal;
import reactor.core.publisher.Flux;

/**
 * The Class RobotKalmanFilter.
 *
 * @author Rimon Oz
 */
@Value
public class RobotKalmanFilter<I extends Player.SetSupplier & SetSupplier & Field.Supplier & Ball.SetSupplier & Referee.Supplier & Temporal>
    implements Deducer<I, Set<Player>> {

  private final KalmanFilter kalmanFilter = new KalmanFilter();

  public static final INDArray MEASUREMENT_TRANSITION_MATRIX = Nd4j.create(
      new double[]{
          1, 0, 0, 0, 0, 0, 0,
          0, 1, 0, 0, 0, 0, 0,
          0, 0, 1, 0, 0, 0, 0,
          0, 0, 0, 1, 0, 0, 0,
          0, 0, 0, 0, 1, 0, 0,
          0, 0, 0, 0, 0, 1, 0,
          0, 0, 0, 0, 0, 0, 1
      },
      new int[]{7, 7});
  public static final INDArray MEASUREMENT_COVARIANCE_MATRIX = Nd4j.create(
      new double[]{
          0.01f, 0, 0, 0, 0, 0, 0,
          0, 0.01f, 0, 0, 0, 0, 0,
          0, 0, 0.01f, 0, 0, 0, 0,
          0, 0, 0, 0.01f, 0, 0, 0,
          0, 0, 0, 0, 0.01f, 0, 0,
          0, 0, 0, 0, 0, 0.01f, 0,
          0, 0, 0, 0, 0, 0, 0.01f
      },
      new int[]{7, 7});
  public static final INDArray CONTROL_TRANSITION_MATRIX = Nd4j.create(
      new double[]{
          1, 0, 0, 0, 0, 0, 0,
          0, 1, 0, 0, 0, 0, 0,
          0, 0, 1, 0, 0, 0, 0,
          0, 0, 0, 1, 0, 0, 0,
          0, 0, 0, 0, 1, 0, 0,
          0, 0, 0, 0, 0, 1, 0,
          0, 0, 0, 0, 0, 0, 1
      },
      new int[]{7, 7});
  public static final INDArray PROCESS_COVARIANCE_MATRIX = Nd4j.create(
      new double[]{
          0.01f, 0, 0, 0, 0, 0, 0,
          0, 0.01f, 0, 0, 0, 0, 0,
          0, 0, 0.01f, 0, 0, 0, 0,
          0, 0, 0, 0.01f, 0, 0, 0,
          0, 0, 0, 0, 0.01f, 0, 0,
          0, 0, 0, 0, 0, 0.01f, 0,
          0, 0, 0, 0, 0, 0, 0.01f
      },
      new int[]{7, 7});

  @Override
  public Publisher<Set<Player>> apply(final Publisher<I> inputPublisher) {
    return Flux.from(inputPublisher)
        .scan(Collections.emptySet(),
            (previousResult, input) ->
                input.getAgents().stream()
                    .map(player ->
                        previousResult.stream()
                            .filter(foundPlayer ->
                                foundPlayer.equals(player))
                            .findFirst()
                            .<Player>map(foundPlayer -> new Player.State(
                                player.getId(),
                                this.kalmanFilter.apply(
                                    getStateTransitionMatrix(
                                        (input.getReferee().getTimestamp()
                                            - foundPlayer.getTimestamp()) / 1000000d),
                                    MEASUREMENT_TRANSITION_MATRIX,
                                    CONTROL_TRANSITION_MATRIX,
                                    Nd4j.zeros(7, 1),
                                    PROCESS_COVARIANCE_MATRIX,
                                    new SimpleDistribution(
                                        player.getState().getMean(),
                                        MEASUREMENT_COVARIANCE_MATRIX),
                                    foundPlayer.getState()),
                                player.getTeamColor()))
                            .orElse(player))
                    .collect(Collectors.toSet()));
  }

  public static INDArray getStateTransitionMatrix(final double timeDifference) {
    return Nd4j.create(
        new double[]{
            1, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, timeDifference, 0, 0,
            0, 0, 1, 0, 0, timeDifference, 0,
            0, 0, 0, 1, 0, 0, timeDifference,
            0, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 1
        },
        new int[]{7, 7});
  }
}

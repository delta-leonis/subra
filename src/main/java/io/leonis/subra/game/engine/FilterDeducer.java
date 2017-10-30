package io.leonis.subra.game.engine;

import io.leonis.subra.game.engine.FilterDeducer.FilteredGameState;
import io.leonis.subra.math.filter.*;
import io.leonis.zosma.game.engine.*;
import java.util.Set;
import lombok.*;
import org.reactivestreams.Publisher;
import io.leonis.subra.game.data.*;
import io.leonis.algieba.Temporal;
import reactor.core.publisher.Flux;

/**
 * The Class FilterDeducer.
 *
 * This class represents an {@link Deducer} which applies noise filtering and/or state estimation on
 * raw ssl-vision data.
 *
 * @author Rimon Oz
 */
@Value
@AllArgsConstructor
public class FilterDeducer<I extends Player.SetSupplier & Goal.SetSupplier & Field.Supplier & Ball.SetSupplier & Referee.Supplier & Temporal>
    implements Deducer<I, FilteredGameState> {
  private final Deducer<I, Ball> ballFilter;
  private final Deducer<I, Set<Player>> robotFilter;

  /**
   * Constructs a new FilterDeducer with a set of first-order {@link BallKalmanFilter} and {@link
   * RobotKalmanFilter}.
   */
  public FilterDeducer() {
    this(new BallKalmanFilter<>(), new RobotKalmanFilter<>());
  }

  @Override
  public Publisher<FilteredGameState> apply(final Publisher<I> soccerGamePublisher) {
    return Flux.from(soccerGamePublisher)
        .transform(new ParallelDeducer<>(
            new Deducer.Identity<>(),
            this.getBallFilter(),
            this.getRobotFilter(),
            FilteredGameState::build));
  }

  @Value
  public static class FilteredGameState
      implements Player.SetSupplier, Goal.SetSupplier, Field.Supplier, Ball.Supplier,
      Referee.Supplier, Temporal {
    private final Set<Player> agents;
    private final Set<Goal> goals;
    private final Ball ball;
    private final Field field;
    private final Referee referee;
    private final long timestamp = System.currentTimeMillis();

    public static <I extends Player.SetSupplier & Goal.SetSupplier & Field.Supplier & Ball.SetSupplier & Referee.Supplier & Temporal>
    FilteredGameState build(final I unfilteredGameState, final Ball ball, final Set<Player> robots) {
      return new FilteredGameState(
          robots,
          unfilteredGameState.getGoals(),
          ball,
          unfilteredGameState.getField(),
          unfilteredGameState.getReferee());
    }
  }
}

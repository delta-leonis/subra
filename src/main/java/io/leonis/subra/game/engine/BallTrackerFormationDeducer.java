package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.*;
import io.leonis.subra.game.formations.PositionFormation;
import io.reactivex.functions.*;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Value;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * The Class BallTrackerFormationDeducer.
 *
 * This class represents a {@link Function} which builds a {@link PositionFormation} which places
 * {@link Player} formation positions on a circle around the ball.
 *
 * @author Rimon Oz
 */
@Value
public class BallTrackerFormationDeducer
    implements BiFunction<Set<Player>, Ball, PositionFormation> {
  private final TeamColor teamColor;
  private final double distanceFromBall;

  @Override
  public PositionFormation apply(final Set<Player> players, final Ball ball) {
    return new PositionFormation(
        players.stream()
            .filter(player -> player.getTeamIdentity().equals(this.getTeamColor()))
            .collect(Collectors.toMap(
                Player::getIdentity,
                player ->
                    Nd4j.vstack(
                        ball.getXY()
                            .add(Transforms
                                .unitVec(ball.getXY().sub(player.getXY()))
                                .mul(this.getDistanceFromBall())),
                        Nd4j.create(new double[]{
                            Math.acos(Transforms.cosineSim(
                                player.getXY(),
                                ball.getXY()))})))));
  }
}

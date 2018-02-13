package io.leonis.subra.ipc.serialization.protobuf;

import com.google.common.collect.ImmutableSet;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Referee.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Referee.SSL_Referee;
import org.robocup.ssl.Referee.SSL_Referee.TeamInfo;
import reactor.core.publisher.Flux;

/**
 * The Class SSLRefboxDeducer
 *
 * Reads {@link SSL_Referee} and formats this into a {@link Referee}.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class SSLRefboxDeducer<I extends SSLRefereeSupplier>
    implements Deducer<I, Referee.Supplier> {

  @Override
  public Publisher<Referee.Supplier> apply(final Publisher<I> refboxPublisher) {
    return Flux.from(refboxPublisher)
        .map(SSLRefereeSupplier::getSSLReferee)
        .map(packet -> () -> new Referee.State(
            packet.getPacketTimestamp(),
            Stage.valueOf(packet.getStage().name()),
            packet.getStageTimeLeft(),
            Command.valueOf(packet.getCommand().name()),
            ImmutableSet.of(
                this.createTeam(TeamColor.BLUE, packet.getBlue(), packet.getPacketTimestamp()),
                this.createTeam(TeamColor.YELLOW, packet.getYellow(), packet.getPacketTimestamp())),
            packet.getCommandTimestamp(),
            packet.getBlueTeamOnPositiveHalf() ? TeamColor.BLUE : TeamColor.YELLOW,
            packet.getBlueTeamOnPositiveHalf() ? TeamColor.YELLOW : TeamColor.BLUE,
            packet.getCommandCounter()));
  }

  private Team createTeam(
      final TeamColor teamColor,
      final TeamInfo teamInfo,
      final long timestamp
  ) {
    return new Team.State(
        timestamp,
        teamInfo.getName(),
        teamInfo.getScore(),
        teamInfo.getRedCards(),
        teamInfo.getYellowCardTimesCount(),
        Collections.unmodifiableList(teamInfo.getYellowCardTimesList()),
        teamInfo.getTimeouts(),
        teamInfo.getTimeoutTime(),
        teamInfo.getGoalie(),
        teamColor);
  }
}

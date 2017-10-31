package io.leonis.subra.ipc.serialization.protobuf;

import com.google.common.collect.ImmutableSet;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Referee.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.robocup.ssl.RefereeOuterClass.Referee.TeamInfo;
import reactor.core.publisher.Flux;

/**
 * The Class SSLRefboxDeducer
 *
 * Reads {@link org.robocup.ssl.RefereeOuterClass.Referee} and formats this into a {@link
 * org.robocup.ssl.RefereeOuterClass.Referee}
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class SSLRefboxDeducer
    implements Deducer<org.robocup.ssl.RefereeOuterClass.Referee, Referee> {
  @Override
  public Publisher<Referee> apply(
      final Publisher<org.robocup.ssl.RefereeOuterClass.Referee> refboxPublisher) {
    return Flux.from(refboxPublisher)
        .map(packet -> new Referee.State(
            packet.getPacketTimestamp(),
            Stage.valueOf(packet.getStage().name()),
            packet.getStageTimeLeft(),
            Command.valueOf(packet.getCommand().name()),
            ImmutableSet.of(
                this.createTeam(TeamColor.BLUE, packet.getBlue(), packet.getPacketTimestamp()),
                this.createTeam(TeamColor.YELLOW, packet.getYellow(), packet.getPacketTimestamp())),
            packet.getCommandTimestamp(),
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

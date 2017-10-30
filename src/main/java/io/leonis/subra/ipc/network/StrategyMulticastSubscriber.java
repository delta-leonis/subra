package io.leonis.subra.ipc.network;

import io.leonis.subra.game.data.Strategy;
import io.leonis.subra.protocol.Robot;
import io.leonis.zosma.ipc.ip.MulticastSubscriber;
import java.net.DatagramPacket;
import lombok.Value;
import org.reactivestreams.*;

/**
 * The Class StrategyMulticastSubscriber.
 *
 * This class describes a {@link Subscriber} of {@link Strategy.Supplier} which encodes them with
 * protobuf and sends them over a {@link MulticastSubscriber multicast network}.
 *
 * @author Rimon Oz
 */
@Value
public class StrategyMulticastSubscriber<S extends Strategy.Supplier> implements
    Subscriber<S> {
  private final MulticastSubscriber targetNetwork;

  @Override
  public void onSubscribe(final Subscription subscription) {
    subscription.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(final S strategy) {
    strategy.getStrategy().entrySet().stream()
        .map(robotCommandEntry ->
            Robot.Command.newBuilder()
                .setRobotId(robotCommandEntry.getKey().getId())
                .setMove(Robot.Command.Move.newBuilder()
                    .setX(robotCommandEntry.getValue().getVelocityX())
                    .setY(robotCommandEntry.getValue().getVelocityY())
                    .setR(robotCommandEntry.getValue().getVelocityR())
                    .build())
                .setAction(Robot.Command.Action.newBuilder()
                    .setKick(robotCommandEntry.getValue().getFlatKick())
                    .setChip(robotCommandEntry.getValue().getChipKick())
                    .setDribble(robotCommandEntry.getValue().getDribblerSpin())
                    .build())
                .build()
        )
        .map(message -> new DatagramPacket(message.toByteArray(), message.getSerializedSize()))
        .forEach(this.targetNetwork::onNext);
  }

  @Override
  public void onError(final Throwable throwable) {
    this.targetNetwork.onError(throwable);
  }

  @Override
  public void onComplete() {
    this.targetNetwork.onComplete();
  }
}

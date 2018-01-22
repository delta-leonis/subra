package io.leonis.subra.ipc.peripheral;

import static java.util.stream.Collectors.*;

import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer.Frame;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.ControllerIdentity;
import io.leonis.zosma.ipc.peripheral.component.Dpad;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.*;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * @author jeroen.dejong.
 */
@Value
public class ChangeAssignmentDeducer<
    I extends Identity,
    C extends Controller & Dpad.Supplier,
    T extends Controller.MapSupplier<I> & Controller.SetSupplier<C>>
implements Deducer<T, Frame<I>> {

  private Comparator<I> comparator;

  private BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> f = (a, b) -> {
    List<Entry<I, Set<ControllerIdentity>>> metsj = a.entrySet().stream()
        .sorted((entry, entry2)-> comparator.compare(entry.getKey(), entry2.getKey())).collect(toList());
    return Collections.emptyMap();
  };

  @Override
  public Publisher<Frame<I>> apply(final Publisher<T> tPublisher) {
    return Flux.from(tPublisher)
        .map(c -> c.getControllerSet().stream()
            .reduce(c.getControllerMapping(),
                f,
                (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                    .flatMap(Collection::stream)
                    .flatMap(entry -> entry.getValue().stream().map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                    .collect(groupingBy(SimpleImmutableEntry::getKey,
                        mapping(SimpleImmutableEntry::getValue, toSet())))))
        .map(Frame::new);
  }

  @Value
  static class Frame<I extends Identity> implements Controller.MapSupplier<I> {
    private final Map<I, Set<ControllerIdentity>> controllerMapping;
  }
}

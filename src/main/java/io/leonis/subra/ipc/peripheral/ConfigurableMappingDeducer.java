package io.leonis.subra.ipc.peripheral;

import static java.util.stream.Collectors.*;

import com.google.common.collect.ImmutableSet;
import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer.Frame;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.*;
import io.leonis.zosma.ipc.peripheral.component.Dpad;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.*;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * @author jeroen.dejong.
 * todo This class should probably not return a Controller.SetSupplier, just to reduce it's responsibility.
 */
@Value
public class ChangeAssignmentDeducer<
    I extends Identity,
    C extends Controller & Dpad.Supplier,
    M extends Controller.MapSupplier<I>,
    S extends Controller.SetSupplier<C>>
implements Deducer<S, Frame<I>> {

  private final AtomicReference<Map<I, Set<ControllerIdentity>>> currentMapping =
      new AtomicReference<>(Collections.emptyMap());
  private final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner;

  public ChangeAssignmentDeducer(
      final Publisher<M> mapSource,
      final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner
  ) {
    // Override current mapping with whatever comes through the publisher.
    Flux.from(mapSource)
        .map(MapSupplier::getControllerMapping)
        .subscribe(currentMapping::set);
    this.combiner = combiner;
  }

  @Override
  public Publisher<Frame<I>> apply(final Publisher<S> setPublisher) {
    return Flux.from(setPublisher)
          .map(c -> c.getControllerSet().stream()
              .reduce(currentMapping.get(),
                  combiner,
                  (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                      .flatMap(Collection::stream)
                      .flatMap(entry -> entry.getValue().stream().map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                      .collect(groupingBy(Entry::getKey,
                          mapping(Entry::getValue, toSet())))))
          .doOnNext(currentMapping::set) // update currentMap to contain moved controllers
          .map(Frame::new);
  }

  @Value
  public static class Frame<I extends Identity> implements Controller.MapSupplier<I> {
    private final Map<I, Set<ControllerIdentity>> controllerMapping;
  }
}

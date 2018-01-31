package io.leonis.subra.game.data;

/**
 * @author jeroen.dejong.
 */
public enum PlayDirection {
  POSITIVE, NEGATIVE;

  interface Supplier {
    PlayDirection getPlayDirection();
  }
}

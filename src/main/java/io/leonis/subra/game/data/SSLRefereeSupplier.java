package io.leonis.subra.game.data;

import org.robocup.ssl.Referee.SSL_Referee;

/**
 * @author jeroen.dejong.
 */
public interface SSLRefereeSupplier {
  SSL_Referee getSSLReferee();
}

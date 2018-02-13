package io.leonis.subra.game.data;

import org.robocup.ssl.Referee.SSL_Referee;

/**
 * The Interface SSLRefereeSupplier.
 *
 * Contains a {@link SSL_Referee}.
 * 
 * @author Jeroen de Jong
 */
public interface SSLRefereeSupplier {
  
  /**
   * @return The robocup ssl refbox packet. 
   */
  SSL_Referee getSSLReferee();
}

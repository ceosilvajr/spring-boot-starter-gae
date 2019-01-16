package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.SimpleAuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public class SimpleTokenTranslator<T extends SimpleAuthUser> implements TokenTranslator<T> {
  private static final Logger LOG = LoggerFactory.getLogger(SimpleTokenTranslator.class);
  private static final String NAME = "nm";
  private static final String ROLES = "rl";

  private final Clock clock;

  public SimpleTokenTranslator(final Clock clock) {
    this.clock = clock;
  }

  @Override
  public JWTCreator.Builder toJwt(final T user, final JWTCreator.Builder jwtBuilder) {
    final String jwtId = user.getId() + "-" + LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME);

    return jwtBuilder.withJWTId(jwtId)
        .withSubject(user.getId())
        .withClaim(NAME, user.getName())
        .withArrayClaim(ROLES, user.getRoles().toArray(new String[]{}));
  }

  @Override
  public T decode(final DecodedJWT decodedJWT) {
    final String id = decodedJWT.getSubject();
    final String name = decodedJWT.getClaim(NAME).asString();
    final List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);
    final T user = (T) createUser(id).name(name).roles(roles);
    return user;
  }

  public T createUser(final String id) {
    return (T) new SimpleAuthUser(id);
  }
}

package org.knowm.xchange.binance;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import org.knowm.xchange.client.ResilienceRegistries;

public final class BinanceResilience {

  public static final String REQUEST_WEIGHT_RATE_LIMITER = "requestWeight";

  public static final String ORDERS_PER_SECOND_RATE_LIMITER = "ordersPerSecond";

  public static final String ORDERS_PER_DAY_RATE_LIMITER = "ordersPerDay";

  public static final int REQUEST_WEIGHT_PER_MINUTE = 1200;

  public static final int ORDERS_RATE_PER_SECOND = 10;

  public static final int ORDERS_RATE_PER_DAY = 200000;

  private BinanceResilience() {}

  public static ResilienceRegistries createRegistries(
      int requestWeightPerMinute, int ordersRatePerSecond, int ordersRatePerDay) {
    ResilienceRegistries registries = new ResilienceRegistries();
    registries
        .rateLimiters()
        .rateLimiter(
            REQUEST_WEIGHT_RATE_LIMITER,
            RateLimiterConfig.from(registries.rateLimiters().getDefaultConfig())
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(requestWeightPerMinute)
                .build());
    registries
        .rateLimiters()
        .rateLimiter(
            ORDERS_PER_SECOND_RATE_LIMITER,
            RateLimiterConfig.from(registries.rateLimiters().getDefaultConfig())
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(ordersRatePerSecond)
                .build());
    registries
        .rateLimiters()
        .rateLimiter(
            ORDERS_PER_DAY_RATE_LIMITER,
            RateLimiterConfig.from(registries.rateLimiters().getDefaultConfig())
                .timeoutDuration(Duration.ZERO)
                .limitRefreshPeriod(Duration.ofDays(1))
                .limitForPeriod(ordersRatePerDay)
                .build());
    return registries;
  }

  public static ResilienceRegistries createRegistries() {
    return createRegistries(REQUEST_WEIGHT_PER_MINUTE, ORDERS_RATE_PER_SECOND, ORDERS_RATE_PER_DAY);
  }
}

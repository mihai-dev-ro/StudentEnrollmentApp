package common.services

import java.time.Instant

import common.repositories.DateTimeProvider

class InstantProvider extends DateTimeProvider {
  override def now: Instant = Instant.now
}

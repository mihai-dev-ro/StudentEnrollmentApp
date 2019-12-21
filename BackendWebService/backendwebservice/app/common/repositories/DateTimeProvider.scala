package common.repositories

import java.time.Instant

trait DateTimeProvider {
  def now: Instant
}

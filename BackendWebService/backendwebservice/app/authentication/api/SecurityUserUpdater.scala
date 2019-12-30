package authentication.api

import authentication.models.{SecurityUser, SecurityUserId, SecurityUserToUpdate}
import slick.dbio.DBIO

trait SecurityUserUpdater {
  def update(securityUserId: SecurityUserId,
    securityUserToUpdate: SecurityUserToUpdate): DBIO[SecurityUser]
}

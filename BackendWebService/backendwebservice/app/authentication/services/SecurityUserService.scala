package authentication.services

import authentication.api.{SecurityUserCreator, SecurityUserProvider, SecurityUserUpdater}
import authentication.models.{NewSecurityUser, PasswordHash, PlainTextPassword, SecurityUser, SecurityUserId, SecurityUserToUpdate}
import authentication.repositories.SecurityUserRepo
import common.exceptions.MissingModelException
import common.models.Email
import common.repositories.DateTimeProvider
import common.utils.DBIOUtils
import org.mindrot.jbcrypt.BCrypt
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class SecurityUserService(securityUserRepo: SecurityUserRepo,
  dateTimeProvider: DateTimeProvider)(
  implicit private val ec: ExecutionContext)
  extends SecurityUserProvider
  with SecurityUserCreator
  with SecurityUserUpdater {
  override def findById(securityUserId: SecurityUserId): DBIO[SecurityUser] = {
    require(securityUserId != null)

    securityUserRepo.findById(securityUserId)
  }

  override def findByEmailOption(email: Email): DBIO[Option[SecurityUser]] = {
    require(email != null)

    securityUserRepo.findByEmailOption(email)
  }

  override def findByEmail(email: Email): DBIO[SecurityUser] = {
    securityUserRepo.findByEmailOption(email)
      .flatMap(DBIOUtils.optionToDBIO(_,
        new MissingModelException((s"Email-ul '$email' nu este inregistrat"))))
  }

  override def create(newSecUser: NewSecurityUser): DBIO[SecurityUser] = {
    require(newSecUser != null)

    val now = dateTimeProvider.now
    val pwdHash = getPwdHash(newSecUser.password)
    securityUserRepo.insertAndGet(
      SecurityUser(SecurityUserId(-1), newSecUser.email, pwdHash, now, now))
  }

  private def getPwdHash(plainTextPassword: PlainTextPassword): PasswordHash = {
    PasswordHash(BCrypt.hashpw(plainTextPassword.value, BCrypt.gensalt))
  }

  override def update(securityUserId: SecurityUserId, securityUserToUpdate: SecurityUserToUpdate):
  DBIO[SecurityUser] = {
    require (securityUserId != null)

    val now = dateTimeProvider.now

    for {
      securityUser <- securityUserRepo.findById(securityUserId)

      email = securityUserToUpdate.email.getOrElse(securityUser.email)

      pwdHash = securityUserToUpdate.plainTextPassword
        .map(getPwdHash)
        .getOrElse(securityUser.password)

      if securityUser.email != email && securityUser.password != pwdHash

      securityUserToUpdateInDb = securityUser.copy(email = email,
        password = pwdHash,
        updatedAt = now)

      securityUserUpdated <- securityUserRepo.updateAndGet(securityUserToUpdateInDb)
    } yield securityUserUpdated
  }
}

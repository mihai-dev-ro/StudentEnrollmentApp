package authentication.repositories

import java.time.Instant

import authentication.exceptions.NotFoundSecurityUserException
import authentication.models.{PasswordHash, SecurityUser, SecurityUserId}
import common.models.{Email, IdMetaModel, Property}
import common.repositories.mappings.JavaTimeDbMappings
import common.repositories.{BaseRepo, DbConfigHelper, IdTable}
import common.utils.DBIOUtils
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.{DBIO => _, MappedTo => _, Rep => _,TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import scala.concurrent.ExecutionContext

class SecurityUserRepo(implicit val executionContext: ExecutionContext)
  extends BaseRepo[SecurityUserId, SecurityUser, SecurityUserTable] {

  def findByEmailOption(email: Email): DBIO[Option[SecurityUser]] = {
    require(email != null)

    query
      .filter(_.email === email)
      .result
      .headOption
  }

  def findByEmail(email: Email): DBIO[SecurityUser] = {
    findByEmailOption(email)
      .flatMap(DBIOUtils.optionToDBIO(_, new NotFoundSecurityUserException(email.toString)))
  }

  override protected val mappingConstructor: Tag => SecurityUserTable = new SecurityUserTable(_)

  override implicit protected val modelIdMapping: BaseColumnType[SecurityUserId] =
    SecurityUserId.securityUserIdDbMapping

  override protected val metaModelToColumnsMapping: Map[Property[_], SecurityUserTable => Rep[_]] =
    Map(SecurityUserMetaModel.id -> (table => table.id),
      SecurityUserMetaModel.email -> (table => table.email),
      SecurityUserMetaModel.password -> (table => table.password))

  override protected val metaModel: IdMetaModel = SecurityUserMetaModel
}

protected class SecurityUserTable(tag: Tag)
  extends IdTable[SecurityUserId, SecurityUser](tag, "security_users")
  with JavaTimeDbMappings {

  def email: Rep[Email] = column[Email]("Email")

  def password: Rep[PasswordHash] = column("Password")

  def createdAt: Rep[Instant] = column[Instant]("Created_at")

  def updatedAt: Rep[Instant] = column[Instant]("Updated_at")

  def * : ProvenShape[SecurityUser] = (id, email, password, createdAt, updatedAt) <> (SecurityUser.tupled,
    SecurityUser.unapply)
}

private[authentication] object SecurityUserMetaModel extends IdMetaModel {
  override type ModelId = SecurityUserId

  val email: Property[Email] = Property("email")
  val password: Property[PasswordHash] = Property("password")
}

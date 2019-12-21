package common.config

import scala.concurrent.ExecutionContext

trait WithExecutionContext {
  implicit def executionContext: ExecutionContext
}

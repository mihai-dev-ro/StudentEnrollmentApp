package common.config

import scala.concurrent.ExecutionContext

trait WithExecutionContextComponents {
  implicit def executionContext: ExecutionContext
}

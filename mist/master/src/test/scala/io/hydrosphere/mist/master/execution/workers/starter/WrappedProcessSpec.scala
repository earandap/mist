package io.hydrosphere.mist.master.execution.workers.starter

import java.nio.file.Paths

import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

class WrappedProcessSpec extends FunSpec with Matchers {

  it("should return success") {
    val f = WrappedProcess.run(Seq("/bin/bash", "-c", "exit 0")).await()
    Await.result(f, Duration.Inf)
  }

  it("should fail") {
    intercept[Throwable] {
      val f = WrappedProcess.run(Seq("/bin/bash", "-c", "exit 1")).await()
      Await.result(f, Duration.Inf)
    }
  }

  it("should save out") {
    val path = Paths.get("target/wp.out")
    val f = WrappedProcess.run(Seq("echo", "yoyo"))
      .saveOut(path)
      .await()

    Await.result(f, Duration.Inf)
    Thread.sleep(1000)
    Source.fromFile(path.toFile).mkString shouldBe "yoyo\n"
  }
}

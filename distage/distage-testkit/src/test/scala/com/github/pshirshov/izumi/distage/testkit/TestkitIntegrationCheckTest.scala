package com.github.pshirshov.izumi.distage.testkit

import cats.effect.IO
import com.github.pshirshov.izumi.distage.testkit.fixtures.{TestFailingIntegrationResource, TestkitSelftest}
import com.github.pshirshov.izumi.fundamentals.platform.functional.Identity
import distage.TagK

abstract class TestkitIntegrationCheckTest[F[_] : TagK] extends TestkitSelftest[F] {
  "testkit" must {
    "skip test if external resource check failed" in dio {
      _: TestFailingIntegrationResource =>
        fail("This test must be ignored")
    }
  }
}

private class TestkitIntegrationCheckTestIO extends TestkitIntegrationCheckTest[IO]

private class TestkitIntegrationCheckTestIdentity extends TestkitIntegrationCheckTest[Identity]

private class TestkitIntegrationCheckTestZio extends TestkitIntegrationCheckTest[scalaz.zio.IO[Throwable, ?]]

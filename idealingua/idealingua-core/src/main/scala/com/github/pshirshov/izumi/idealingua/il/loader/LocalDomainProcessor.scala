package com.github.pshirshov.izumi.idealingua.il.loader

import java.io.File
import java.nio.file.{Path, Paths}

import com.github.pshirshov.izumi.idealingua.il.ParsedDomain
import com.github.pshirshov.izumi.idealingua.il.loader.LocalModelLoader.ParsedModel
import com.github.pshirshov.izumi.idealingua.model.exceptions.IDLException
import com.github.pshirshov.izumi.idealingua.model.il.{DomainDefinition, DomainId}

protected[loader] class LocalDomainProcessor(root: Path, classpath: Seq[File], domain: ParsedDomain, domains: Map[DomainId, ParsedDomain], models: Map[Path, ParsedModel]) {

  import LocalModelLoader._


  def postprocess(): DomainDefinition = {
    val domainResolver: (DomainId) => Option[ParsedDomain] = toDomainResolver(domains.get)
    val modelResolver: (Path) => Option[ParsedModel] = toModelResolver(models.get)


    val withIncludes = domain
      .includes
      .foldLeft(domain) {
        case (d, toInclude) =>
          val incPath = Paths.get(toInclude)

          modelResolver(incPath) match {
            case Some(inclusion) =>
              d.extend(inclusion)

            case None =>
              throw new IDLException(s"Can't find inclusion $incPath in classpath nor filesystem while operating within $root")
          }
      }
      .copy(includes = Seq.empty)

    val imports = domain
      .imports
      .map {
        p =>
          domainResolver(p) match {
            case Some(d) =>
              d.domain.id -> new LocalDomainProcessor(root, classpath, d, domains, models).postprocess() //postprocess(d, domains, models)

            case None =>
              throw new IDLException(s"Can't find reference $p in classpath nor filesystem while operating within $root")
          }
      }
      .toMap

    val withImports = withIncludes
      .copy(imports = Seq.empty, domain = withIncludes.domain.copy(referenced = imports))

    withImports.domain
  }

  // TODO: decopypaste?
  private def toModelResolver(primary: Path => Option[ParsedModel])(incPath: Path): Option[ParsedModel] = {
    primary(incPath)
      .orElse {
        val fallback = resolveFromCP(incPath, Some("idealingua"), modelExt)
          .orElse(resolveFromCP(incPath, None, modelExt))
          .orElse(resolveFromJars(incPath))
          .orElse(resolveFromJavaCP(incPath))

        fallback.map {
          src =>
            parseModels(Map(incPath -> src))(incPath)
        }
      }
  }

  private def toDomainResolver(primary: DomainId => Option[ParsedDomain])(incPath: DomainId): Option[ParsedDomain] = {
    val asPath = toPath(incPath)

    primary(incPath)
      .orElse {
        val fallback = resolveFromCP(asPath, Some("idealingua"), domainExt)
          .orElse(resolveFromCP(asPath, None, domainExt))
          .orElse(resolveFromJars(asPath))
          .orElse(resolveFromJavaCP(asPath))

        fallback.map {
          src =>
            val parsed = parseDomains(Map(asPath -> src))
            parsed(incPath)
        }
      }
  }

  private def resolveFromJars(incPath: Path): Option[String] = {
    classpath
      .filter(_.isFile)
      .find(f => false) // TODO: support jars!
      .map(path => readFile(path.toPath))
  }

  private def resolveFromCP(incPath: Path, prefix: Option[String], ext: String): Option[String] = {
    val allCandidates = (Seq(root, root.resolve(toPath(domain.domain.id)).getParent).map(_.toFile) ++ classpath)
      .filter(_.isDirectory)
      .flatMap {
        directory =>
          val base = prefix match {
            case None =>
              directory.toPath
            case Some(v) =>
              directory.toPath.resolve(v)

          }

          val candidatePath = base.resolve(incPath)
          val candidates = Seq(candidatePath)
          candidates.map(_.toFile)
      }

    val result = allCandidates
      .find(f => f.exists() && !f.isDirectory)
      .map(path => readFile(path.toPath))
    result
  }

  private def resolveFromJavaCP(incPath: Path): Option[String] = {
    Option(getClass.getResource(Paths.get("/idealingua/").resolve(incPath).toString))
      .map {
        fallback =>
          readFile(new File(fallback.toURI).toPath)
      }
  }
}
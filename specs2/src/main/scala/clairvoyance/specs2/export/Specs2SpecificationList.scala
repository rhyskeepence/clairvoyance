package clairvoyance.specs2.export

import org.specs2.main.Arguments
import org.specs2.reporter.DefaultStatisticsRepository
import org.specs2.specification._

import scala.util.Properties._
import scala.xml.NodeSeq

object Specs2SpecificationList extends DefaultStatisticsRepository {

  def list(structures: Seq[SpecificationStructure])(implicit args: Arguments): NodeSeq = {
    def fragmentsOf(s: SpecificationStructure): Seq[Fragment] =
      s.formatFragments(s.map(Fragments.withCreationPaths(Fragments.withSpecName(s.is, s))))
        .fragments

    def resultOfExample(id: SpecIdentification, exampleName: String): NodeSeq = {
      <li><span> - {exampleName}</span></li>
    }

    val structure = structures.map { s =>
      val stats = s.identification match {
        case name: SpecName => getStatistics(name)
      }

      val cssClass = stats
        .map(stats => {
          if (stats.isSuccess) "test-passed"
          else if (stats.hasSuspended) "test-not-run"
          else "test-failed"
        })
        .getOrElse("test-not-run")

      val specFragments = fragmentsOf(s).flatMap {
        case Text(text, _) => Some(<li><em>{formatShortExampleName(text.raw)}</em></li>)
        case Example(name, _, _, _, _) =>
          Some(resultOfExample(s.identification, formatShortExampleName(name.raw)))
        case _ => None
      }

      <a href={s.identification.url}>
        <ul class={cssClass}>
          <li><span class="specificationTitle">{s.identification.title}</span> ({
        stats.map(_.displayResults).map(args.colors.removeColors).getOrElse("Not Run")
      })</li>
        {specFragments}
        </ul>
      </a>
    }

    <ul>{structure}</ul>
  }

  protected def formatShortExampleName: String => String = _.split(lineSeparator).head

}

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes
import akka.stream.Attributes
import akka.stream.alpakka.s3.BucketAccess
import akka.stream.alpakka.s3.BucketAccess.AccessGranted
import akka.stream.alpakka.s3.scaladsl.S3
import akka.stream.scaladsl.{Sink, StreamConverters}

import java.io.FileInputStream
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object RunMe {

  val BucketName = "my-bucket"

  def runMe(implicit attributes: Attributes): Unit = {

    implicit val system: ActorSystem = ActorSystem("alpakka-s3-localstack")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val log = system.log

    val makeBucket: Future[Done] = S3.makeBucket(BucketName)
    val checkIfBucketExists: Future[BucketAccess] = makeBucket.flatMap {
      result =>
        require(result == Done)
        log.info(s"makeBucket $result")
        S3.checkIfBucketExists(BucketName)
    }
    val multipartUpload: Future[String] = checkIfBucketExists.flatMap {
      result =>
        require(result == AccessGranted)
        log.info(s"checkIfBucketExists $result")
        val inputStream = new FileInputStream("README.md")
        val source = StreamConverters.fromInputStream(() => inputStream)
        val sink = S3.multipartUpload(
          BucketName,
          "stuff/README.md",
          ContentTypes.`text/plain(UTF-8)`
        )
        source.runWith(sink).map(_.key)
    }
    val download: Future[String] = multipartUpload.flatMap { key =>
      S3.download(BucketName, key).runWith(Sink.head).flatMap {
        case None => sys.error("download")
        case Some((source, _)) =>
          system.log.info(s"downloaded")
          source.map(_.utf8String).runWith(Sink.head)
      }
    }
    download.onComplete { result =>
      result match {
        case Success(data)      => system.log.info(data)
        case Failure(exception) => system.log.error(exception, "complete")
      }
      system.terminate()
    }
  }
}

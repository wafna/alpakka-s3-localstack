import akka.stream.Attributes
import akka.stream.alpakka.s3.{S3Attributes, S3Settings}
import com.typesafe.config.ConfigFactory

object Bad extends App {

  val BucketName = "my-bucket"

  val appConfig = ConfigFactory.load()
  val itConfig = appConfig.getConfig("it")

  implicit val attributes: Attributes = {
    val s3Config = itConfig.getConfig("alpakka.s3")
    val s3Defaults =
      ConfigFactory.load("reference.conf").getConfig("alpakka.s3")
    S3Attributes.settings(S3Settings(s3Config.withFallback(s3Defaults)))
  }

  RunMe.runMe

}

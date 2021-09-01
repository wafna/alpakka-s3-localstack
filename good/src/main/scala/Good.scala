import akka.stream.Attributes

object Good extends App {

  // The default in S3.
  implicit val attributes: Attributes = Attributes()

  RunMe.runMe

}

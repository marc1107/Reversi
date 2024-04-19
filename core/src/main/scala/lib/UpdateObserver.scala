package lib

import scala.io.Source

class UpdateObserver(url: String) extends Observer {
  override def update(e: Event): Unit = {
    val url = this.url + "?event=" + e.toString.toLowerCase
    val result = Source.fromURL(url).mkString
  }
}

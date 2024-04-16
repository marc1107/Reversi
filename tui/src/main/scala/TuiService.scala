import CoreModule.given

object TuiService {
  @main def startTui(): Unit = {
    new TUI().run()
  }
}

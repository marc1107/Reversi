import CoreModule.given

object GuiService {
  @main def startGui(): Unit = {
    new GUI().run()
  }
}
package com.feynmanliang.searchbird

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop

object SearchbirdConsoleClient extends App {
  val settings = new Settings
  settings.usejavacp.value = true

  val intLoop = new ILoop() {
    override def prompt = "\nfinagle-client> "
    override def printWelcome() = {
      val client = new Client(args(0), args(1))
      intp.bind("client", client)
      super.printWelcome()
      echo("'client' is bound to your thrift client.")
    }
  }

  intLoop.process(settings)
}

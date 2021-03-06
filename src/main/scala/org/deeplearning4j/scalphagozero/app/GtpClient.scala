package org.deeplearning4j.scalphagozero.app

import java.io.{ BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter, Writer }

import org.lisoft.gonector.GoTextProtocol
import org.deeplearning4j.scalphagozero.gtp.ScalphaGoEngine

class GtpClient {

  private val engine = ScalphaGoEngine()

  private val reader: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
  private val writer: Writer = new BufferedWriter(new OutputStreamWriter(System.out))

  // Run the protocol parsing loop
  private val gtp: GoTextProtocol = new GoTextProtocol(reader, writer, engine)

  def run(): Unit = {
    val result = gtp.call()
    println("GTP result = " + result)
  }
}

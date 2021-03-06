package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.{ Agent, ZeroAgent }
import org.deeplearning4j.scalphagozero.board._
import org.deeplearning4j.scalphagozero.scoring.GameResult
import org.nd4j.linalg.factory.Nd4j
import GameResult.DEFAULT_KOMI

/**
  * Simulate a game between two player agents.
  *
  * @author Max Pumperla
  */
object ZeroSimulator {

  val DEBUG = false

  def simulateGame(blackAgent: Agent, whiteAgent: Agent, boardSize: Int, komi: Float = DEFAULT_KOMI): Unit = {

    val game = GameState.newGame(boardSize)
    val agents: Map[Player, Agent] = Map(BlackPlayer -> blackAgent, WhitePlayer -> whiteAgent)

    val gameResult = doSimulation(game, agents, komi)
    println(gameResult.toDebugString)
  }

  /**
    * Two agents play against each other and the collectors accumulate the knowledge gained
    * so that it can later be used for training.
    */
  def simulateLearningGame(blackAgent: ZeroAgent, whiteAgent: ZeroAgent, komi: Float = DEFAULT_KOMI): Unit = {

    val encoder = blackAgent.encoder
    val boardSize = encoder.boardSize

    val blackCollector = blackAgent.collector
    val whiteCollector = whiteAgent.collector

    val game = GameState.newGame(boardSize)
    val agents: Map[Player, Agent] = Map(
      BlackPlayer -> blackAgent,
      WhitePlayer -> whiteAgent
    )

    blackCollector.beginEpisode()
    whiteCollector.beginEpisode()

    val gameResult = doSimulation(game, agents, komi)

    gameResult.winner match {
      case BlackPlayer =>
        blackCollector.completeEpisode(Nd4j.scalar(1))
        whiteCollector.completeEpisode(Nd4j.scalar(-1))
      case WhitePlayer =>
        blackCollector.completeEpisode(Nd4j.scalar(-1))
        whiteCollector.completeEpisode(Nd4j.scalar(1))
    }
  }

  /** The 2 agents play a game against either other */
  private def doSimulation(initialState: GameState, agents: Map[Player, Agent], komi: Float): GameResult = {
    println(">>> Starting a new game.")
    var game = initialState
    while (!game.isOver) {
      val nextMove = agents(game.nextPlayer).selectMove(game)

      if (game.isValidMove(nextMove)) {
        println(game.nextPlayer + " " + nextMove.toString)
        game = game.applyMove(nextMove)
        println(game.board)
      } else {
        println(game.nextPlayer + " made an invalid move: " + nextMove + ". Try again.")
      }
    }
    println(">>> Simulation finished.")
    println()
    game.gameResult(komi).get
  }
}

package org.deeplearning4j.scalphagozero.board

import org.deeplearning4j.scalphagozero.board.Move.Play
import org.scalatest.funspec.AnyFunSpec

class GameStateTest extends AnyFunSpec {

  describe("Starting a new 19x19 game") {
    val start = GameState.newGame(19)
    it("should apply moves") {
      val nextState = start.applyMove(Move.Play(Point(16, 16)))

      assert(start == nextState.previousState.get)
      assert(nextState.board.getPlayer(Point(16, 16)).get == BlackPlayer)
      assert(nextState.nextPlayer == WhitePlayer)
    }
  }

  describe("Detect ko in a 5x5 game") {
    var game = GameState.newGame(5)

    it("Apply moves, until ko rule is take the first time") {
      game = game.applyMove(Play(3, 3))
      game = game.applyMove(Play(3, 4))
      game = game.applyMove(Play(4, 4))
      game = game.applyMove(Play(4, 5))
      game = game.applyMove(Play(2, 4))
      game = game.applyMove(Play(2, 5))
      val previous = game
      game = game.applyMove(Play(Point(3, 5))) // initial ko capture
      println("After initial ko: " + game.board)

      assert(previous == game.previousState.get)
      assert(game.board.getPlayer(Point(3, 5)).get == BlackPlayer)
      assert(game.board.getPlayer(Point(3, 4)).isEmpty)
    }

    it("Ko is not allowed to be immediately retaken") {
      println("After initial ko2: " + game.board)
      assert(game.doesMoveViolateKo(WhitePlayer, Play(3, 4)))
    }
  }

  describe("No winner at start of the game") {
    var game = GameState.newGame(5)
    it("should have no winner yet") {
      assert(game.gameResult().isEmpty)
    }

    game = game.applyMove(Play(3, 3))
    game = game.applyMove(Play(3, 4))
    game = game.applyMove(Play(4, 4))
    game = game.applyMove(Play(4, 5))
    it("should have no winner after a few moves played") {
      assert(game.gameResult().isEmpty)
    }
  }

  describe("Won after black player resigns") {
    var game = GameState.newGame(5)
    game = game.applyMove(Play(3, 3)) // black play
    game = game.applyMove(Play(2, 3)) // white play

    it("should be a white win by resignation") {
      game = game.applyMove(Move.Resign)
      val result = game.gameResult().get
      assert(result.winner == WhitePlayer)
      assert(result.toString == "White won by resignation")
    }
  }

  describe("Won after white player resigns") {
    var game = GameState.newGame(5)
    game = game.applyMove(Play(3, 3))
    it("should be a black win") {
      game = game.applyMove(Move.Resign)
      val result = game.gameResult().get
      assert(result.winner == BlackPlayer)
      assert(result.toString == "Black won by resignation")
    }
  }

  describe("Won only after both players pass") {
    var game = GameState.newGame(5)
    game = game.applyMove(Play(3, 3))
    game = game.applyMove(Move.Pass)
    it("should have no winner yet") {
      assert(game.gameResult().isEmpty)
    }

    it("should have winner after the second consecutive pass") {
      game = game.applyMove(Move.Pass)
      // black wins because they have one more stone on the board.
      val result = game.gameResult().get
      assert(result.winner == BlackPlayer)
      assert(result.toString == "Black +24.5")
    }
  }
}

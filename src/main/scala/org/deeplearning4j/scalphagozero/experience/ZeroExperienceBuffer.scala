package org.deeplearning4j.scalphagozero.experience

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

/**
  * AlphaGo Zero experience buffer combines states, visit counts and rewards as single INDArrays
  *
  * @param states encoded game states as INDArray
  * @param visitCounts visitCounts as INDArray
  * @param rewards rewards as INDArray
  *
  * @author Max Pumperla
  */
final case class ZeroExperienceBuffer(states: INDArray, visitCounts: INDArray, rewards: INDArray)

object ZeroExperienceBuffer {

  def combineExperience(collectors: List[ZeroExperienceCollector]): ZeroExperienceBuffer = {
    val myStates = collectors.flatMap(_.states)
    val myRewards = collectors.flatMap(_.rewards)
    val myVisitCounts = collectors.flatMap(_.visitCounts)

    val combinedStates = Nd4j.concat(0, myStates: _*)
    val combinedRewards = Nd4j.concat(0, myRewards: _*)
    val combinedVisitCounts = Nd4j.concat(0, myVisitCounts: _*)

    collectors.foreach(_.clearAllBuffers())

    ZeroExperienceBuffer(combinedStates, combinedVisitCounts, combinedRewards)
  }
}


package it.unibo.pps2017.client.controller

import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerReference, SocialMap}

import scala.language.implicitConversions

package object socialcontroller {

  /**
    * Implicit conversion from Server model to client one.
    *
    * @param socialMap the map of online players.
    * @return a list containing map tuples converted in PlayerReferences.
    */
  implicit def convertSocialMapToPlayerRefList(socialMap: SocialMap): List[PlayerReference] =
    socialMap.toList.map(tuple => PlayerReference(tuple._1, tuple._2))
}

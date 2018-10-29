package it.unibo.pps2017.client.model.actors.socialactor.controllers

import it.unibo.pps2017.commons.remote.social.SocialUtils.FriendList

class SocialListController extends MockSocialController {

  var friendList: FriendList = _
  var onlinePlayerList: FriendList = _

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = this.onlinePlayerList = playerRefList

  override def updateOnlineFriendsList(friendList: FriendList): Unit = this.friendList = friendList

}

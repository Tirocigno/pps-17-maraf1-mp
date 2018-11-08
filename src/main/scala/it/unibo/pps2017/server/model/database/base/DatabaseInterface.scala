package it.unibo.pps2017.server.model.database.base

trait DatabaseInterface {
  /**
    * Retrieve the ranking from the database.
    *
    * @param onSuccess
    * On query success.
    * @param onFail
    * error handler.
    * @param from
    * starting position.
    * @param to
    * ending position.
    */
  def getRanking(onSuccess: Seq[(String, Double)] => Unit,
                 onFail: Throwable => Unit,
                 from: Option[Long],
                 to: Option[Long]): Unit
}

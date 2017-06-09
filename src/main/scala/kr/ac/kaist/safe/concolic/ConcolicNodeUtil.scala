package kr.ac.kaist.safe.concolic

object ConcolicNodeUtil {

  val concolicPrefix = "<>Concolic<>"
  def freshConcolicName(n: String) = concolicPrefix + n

}


package it.unibo.pps2017.core.deck

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleDeckTest extends FunSuite {

  /**
    * Check if the order between two sequences is different.
    *
    * @param baseSeq  the first sequence.
    * @param otherSeq the second sequence to compare.
    * @tparam A generic type of the sequence.
    * @return true if at least one element in the same position is different, false otherwise.
    */
  private[this] def isOrderDifferent[A](baseSeq: Seq[A], otherSeq: Seq[A]): Boolean = baseSeq match {
    case SeqExtractor(h, t) if h equals otherSeq.head => isOrderDifferent(t, otherSeq.tail)
    case SeqExtractor(_, _) => true
    case _ => false
  }

  /**
    * Test to check the size of the default deck.
    */
  test("Size of Default deck") {
    assert(SimpleDeck.generateDefaultCardsList().size == defaultDeckSize)
  }

  /**
    * Test that create a simple deck and check if exist.
    */
  test("Creation of SimpleDeck") {
    assert(Option(SimpleDeck()).isDefined)
  }

  test("Size of Deck inside SimpleDeckImpl") {
    assert(SimpleDeck().cardList.size == defaultDeckSize)
  }

  test("Shuffle test") {
    val baseDeck = SimpleDeck()
    baseDeck.shuffle()
    val otherCardSeq = SimpleDeck().cardList
    assert(baseDeck.cardList.compareSequence(otherCardSeq)(isOrderDifferent))
  }
}

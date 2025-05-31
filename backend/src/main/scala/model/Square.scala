package model

import scala.util.{Failure, Try}

object Square {
  private val square = raw"([a-h])([1-8])".r

  def fromStandardName(name: String): Try[Square] = name match {
    case square(file, rank) =>
      Try(Square(file.toCharArray.head.toInt - 96, rank.toInt))
    case _ => Failure(new IllegalArgumentException(s"Malformed square: $name"))
  }
}

/** One of 64 squares on the board. Has coordinates in terms of rank (y) and
  * file (x). Files are numerical instead of alphabetical for easier processing.
  * A-file is 1. TODO(hinderson): swap rank and file order in ctor
  *
  * @param file
  *   x coordinate, 1-indexed.
  * @param rank
  *   y coordinate, 1-indexed.
  */
case class Square(file: Int, rank: Int) {

  /** Get the square resulting from changing this square's file by the given
    * amount.
    *
    * @param delta
    *   the amount (positive or negative) to increment the file.
    * @return
    *   the square resulting from the change.
    */
  def changeFile(delta: Int): Square = {
    Square(file + delta, rank)
  }

  /** Get the square resulting from changing this square's rank by the given
    * amount.
    *
    * @param delta
    *   the amount (positive or negative) to increment the rank.
    * @return
    *   the square resulting from the change.
    */
  def changeRank(delta: Int): Square = {
    Square(file, rank + delta)
  }

  /** The standard name for this square's file, from a-h
    *
    * @return
    *   the standard name for this square's file.
    */
  def standardFileName: String = s"${(file + 96).toChar}"

  /** The standard name for this square, using the standard file name and the
    * rank.
    *
    * @return
    *   the standard name for this square.
    */
  def standardName: String = s"$standardFileName$rank"
}

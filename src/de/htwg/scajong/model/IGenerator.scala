package de.htwg.scajong.model

abstract class IGenerator {
  def generate(field:Field)
  def scramble(fiedl:Field)
}
package scajong.model

abstract class IGenerator {
  def generate(field:Field, setupFile:String)
  def scramble(fiedl:Field)
}
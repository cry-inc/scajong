package scajong.model

abstract class IGenerator {
  def generate(field:Field, setupFile:String, tileFile:String)
  def scramble(fiedl:Field)
}
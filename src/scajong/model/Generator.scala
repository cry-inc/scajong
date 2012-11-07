package scajong.model

trait Generator {
  def generate(game:Game, setupFile:String)
  def scramble(game:Game)
}
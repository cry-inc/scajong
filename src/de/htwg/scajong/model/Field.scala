package de.htwg.scajong.model

class Field {
	val Width = 60;
	val Height = 40;
	var tiles : Map[Int, Tile] = Map()
	
	private def CalcTileIndex(tile:Tile):Int = {
	  tile.x + tile.y
	}
}
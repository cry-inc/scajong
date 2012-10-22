package de.htwg.scajong.model

class ReverseGenerator(val setupFile:String, val tileFile:String) extends IGenerator {
  def scramble(field:Field) {
    /*
    List<TilePair> reversed = new List<TilePair>();
    while (field.Tiles.Count > 0)
    {
        // Find two or more random outer tiles, remove them and store the coords
        List<Tile> removables = new List<Tile>();
        removables.AddRange(ExtractRemovableTiles(field));

        // Continue until no more removable tile pairs are left in the list
        while (removables.Count > 1)
            reversed.Add(TilePair.FetchPair(removables));

        // Add remaing tile to the field
        foreach (Tile tile in removables)
            field.Add(tile);
    }

    // Read the list from behind and and get random a random tile type for each pair to build the game
    Random random = new Random();
    for (int i = reversed.Count - 1; i >= 0; i--)
    {
        int typeIndex = random.Next() % field.Types.Length;
        reversed[i].Tile1.Type = field.Types[typeIndex];
        reversed[i].Tile2.Type = field.Types[typeIndex];
        field.Add(reversed[i].Tile1);
        field.Add(reversed[i].Tile2);
    }     
    */
  }
  
  def generate(field:Field) {
    field.tileTypes = TileType.LoadTileTypes(tileFile)
    field.tiles = Map()
    
    // Place the full set without a tile type
    loadStructure(field, null, setupFile);

    // Set the tile types in a solveable order
    scramble(field);
  }
  
  def loadStructure(field:Field, tileType:TileType, setupFile:String) {
    /*
    string[] lines = File.ReadAllLines(path);
    if (lines.Length % 2 != 0)
        throw new Exception("Invalid odd tile count!");

    for (line <- lines) {
        string[] splitted = line.Split(' ');
        if (splitted.Length != 3) continue;
        Tile tile = new Tile(int.Parse(splitted[0]), int.Parse(splitted[1]), int.Parse(splitted[2]), type);
        field.Add(tile);
    }
    */
  }
  
  def extractRemovableTiles(field:Field) : List[Tile] = {
    /*
    List<Tile> removables = new List<Tile>();

    int[] keys = new int[field.Tiles.Count];
    field.Tiles.Keys.CopyTo(keys, 0);

    foreach (int key in keys)
        if (field.CanMove(field.Tiles[key]))
            removables.Add(field.Tiles[key]);

    foreach (Tile tile in removables)
        field.Remove(tile);

    return removables;
    */
    Nil
  }
}
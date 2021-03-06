package scajong.view.jetty

import scajong.model._
import scajong.view._
import scajong.util._
import scajong.controller._
import util.matching.Regex
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.continuation.Continuation
import org.eclipse.jetty.continuation.ContinuationSupport

class JsonNotification(val name:String, val id:Int, val param1:String = "", val param2:String = "") {
  override def toString = {
    "        {\n" +
    "            \"name\": \"" + name + "\",\n" +
    "            \"id\": " + id + ",\n" +
    "            \"param1\": \"" + param1 + "\",\n" +
    "            \"param2\": \"" + param2 + "\"\n" +
    "        }"
  }
}

class JettyView(port:Int = 8888) extends AbstractHandler with View {
  
  private var controller:Controller = null
  private var notificationId = 1
  private val server = new Server(port)
  private var notifications = List[JsonNotification]()
  private var continuations = List[Continuation]()
  private var currentSetup:Setup = null
  private var currentMs = 0
  private var selectedTile:Tile = null
  
  server.setHandler(this);
  
  override def autoClose = true
  
  private def addNotification(name:String, param1:String = "", param2:String = "") {
    val notification = new JsonNotification(name, notificationId, param1, param2)
    notificationId += 1
    notifications = notification :: notifications
    if (notifications.length > 15)
      notifications = notifications.take(15)
    continuations.foreach(_.resume)
    continuations = List[Continuation]()
  }
  
  notificationProcessor = {
    case NoFurtherMovesNotification() => addNotification("NoFurtherMoves")
    case TilesRemovedNotification(tiles) => addNotification("UpdateField")
    case ScrambledNotification() => addNotification("UpdateField")
    case TileSelectedNotification(tile) => selectedTile = tile; addNotification("UpdateField")
    case CreatedGameNotification() => addNotification("NewGame")
    case NewScoreBoardEntryNotification(setup, position) => addNotification("ShowScore", setup.id, position.toString)
    case WonNotification(setup, ms, inScoreBoard) => won(setup, ms, inScoreBoard)
    case StartHintNotification(hintPair) => addNotification("StartHint")
    case StopHintNotification() => addNotification("StopHint")
    case StartMoveablesNotification() => addNotification("StartMoveables")
    case StopMoveablesNotification() => addNotification("StopMoveables")
  }
  
  private def won(setup:Setup, ms:Int, inScoreBoard:Boolean) {
    if (inScoreBoard) {
      currentSetup = setup
      currentMs = ms
      addNotification("AddScore")
    } else addNotification("ShowScore", setup.id)
  }
  
  private def s2i(str:String) = {
    try {
      str.toInt
    } catch {
      case _ => 0
    }
  }

  override def handle(target:String, baseRequest:Request, request:HttpServletRequest, response:HttpServletResponse) {    
    response.setStatus(HttpServletResponse.SC_OK)
    var binary = false
    var stringData:String = ""
    var binaryData:Array[Byte] = null
    
    val tileImageRegex = new Regex("^/tiles/([a-z0-9]+)\\.png$", "tile")
    val scoreRegex = new Regex("^/scores/([A-Za-z0-9]+)\\.json$", "setupId")
    val setupImageRegex = new Regex("^/setups/([A-Za-z0-9]+)\\.png$", "setupId")
    val actionRegex = new Regex("^/action/(.+)$", "a")
    val jsRegex = new Regex("^/([a-z0-9]+)\\.js$", "script")
    
    target match {
      case "/index_wgl.html" => {
        response.setContentType("text/html;charset=utf-8")
        stringData = FileUtil.readText("web/index_wgl.html")
      }
      case "/field.json" => {
        response.setContentType("application/json")
        stringData = buildFieldJson
      }
      case "/notifications.json" => {
        val last = s2i(request.getParameter("wait"))
        if (notifications.length > 0 && notifications.head.id <= last) {
          // No new notifications, wait
          val continuation = ContinuationSupport.getContinuation(request)
          continuation.setTimeout(10000)
          continuation.suspend()
          continuations = continuation :: continuations
        } else {
          // New notifications, send answer immediately
          response.setContentType("application/json")
            stringData = buildNotificationsJson
          }
        }
      case "/tiles.json" => {
        response.setContentType("application/json")
        stringData = buildTilesJson
      }
      case "/setups.json" => {
        response.setContentType("application/json")
        stringData = buildSetupsJson
      }
      case tileImageRegex(tile) => {
        val fn = "tiles/" + tile + ".png"
        if (FileUtil.exists(fn)) {
          binary = true
          response.setContentType("image/png")
          binaryData = FileUtil.readBytes(fn)
        } else {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND)
          response.setContentType("text/html;charset=utf-8")
          stringData = "File not found!"
        }
      }
      case setupImageRegex(setupId) => {
        val fn = "setups/" + setupId + ".png"
        if (FileUtil.exists(fn)) {
          binary = true
          response.setContentType("image/png")
          binaryData = FileUtil.readBytes(fn)
        } else {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND)
          response.setContentType("text/html;charset=utf-8")
          stringData = "File not found!"
        }
      }
      case scoreRegex(setupId) => {
        response.setContentType("application/json")
        val setup = controller.setupById(setupId)
        stringData = if (setup != null) buildScoresJson(setup) else "{}"
      }
      case actionRegex(a) => {
        response.setContentType("application/json")
        stringData = action(a)
      }
      case jsRegex(script) => {
        val fn = "web/" + script + ".js"
        if (FileUtil.exists(fn)) {
          response.setContentType("application/x-javascript")
          stringData = FileUtil.readText(fn)
        }
      }
      case _ => {
        response.setContentType("text/html;charset=utf-8")
        stringData = FileUtil.readText("web/index.html")
      }
    }
    
    baseRequest.setHandled(true)
    if (binary)
      response.getOutputStream().write(binaryData)
    else
      response.getWriter().println(stringData)
  }

  override def startView(controller:Controller) {
    this.controller = controller
    server.start
  }

  override def stopView(controller:Controller) {
    server.stop
    server.join
  }

  def buildNotificationsJson = {
    "{\n" +
    "    \"notifications\": [\n" +
         notifications.reverse.mkString(",\n") + "\n" +
    "    ]\n" +
    "}\n"
  }

  def buildFieldJson = {
    var hint = controller.hint
    val tiles = controller.sortedTiles.reverse
    var tilesJson = List[String]()
    tiles.foreach(tile => {
      val selectedStr = if (tile == selectedTile) "true" else  "false"
      val moveableStr = if (controller.canMove(tile)) "true" else "false"
      val hintStr = if (hint != null && (tile == hint.tile1 || tile == hint.tile2)) "true" else "false"
      val tileType = if (tile.tileType == null) "empty" else tile.tileType.name
      val tileTypeId = if (tile.tileType == null) "-1" else tile.tileType.id.toString
      tilesJson = "          {\n" +
                  "              \"x\": " + tile.x + ",\n" +
                  "              \"y\": " + tile.y + ",\n" +
                  "              \"z\": " + tile.z + ",\n" +
                  "              \"type\": \"" + tileType + "\",\n" +
                  "              \"typeId\": " + tileTypeId + ",\n" +
                  "              \"selected\": " + selectedStr + ",\n" +
                  "              \"hint\": " + hintStr + ",\n" +
                  "              \"moveable\": " + moveableStr + "\n" +
                  "          }" :: tilesJson
    })
    
    "{\n" +
    "    \"fieldwidth\": " + controller.fieldWidth + ",\n" +
    "    \"fieldheight\": " + controller.fieldHeight + ",\n" +
    "    \"tilewidth\": " + Tile.Width + ",\n" +
    "    \"tileheight\": " + Tile.Height + ",\n" +
    "    \"tiledepth\": " + Tile.Depth + ",\n" +
    "    \"tiles\": [\n" + tilesJson.mkString(",\n") + "\n" +
    "    ]\n" +
    "}\n"
  }

  def buildTilesJson = {
    var typesJson = List[String]()
    controller.tileTypes.foreach(tileType => {
      typesJson = "       {\n" +
                  "           \"id\": " + tileType.id + ",\n" +
                  "           \"name\": \"" + tileType.name + "\"\n" +
                  "       }" :: typesJson
    })
    
    "{\n" +
    "   \"types\": [\n" +
          typesJson.mkString(",\n") + "\n" +
    "   ]\n" +
    "}\n"
  }

  def buildSetupsJson = {
    var setupsJson = List[String]()
    controller.setups.foreach(setup => {
      setupsJson = "       {\n" +
                   "           \"id\": \"" + setup.id + "\",\n" +
                   "           \"name\": \"" + setup.name + "\"\n" +
                   "       }" :: setupsJson
    })
    
    "{\n" +
    "   \"setups\": [\n" +
    setupsJson.mkString(",\n") + "\n" +
    "   ]\n" +
    "}\n"
  }

  def buildScoresJson(setup:Setup) = {
    val scores = controller.scores.getScores(setup).reverse
    var scoresJson = List[String]()
    scores.foreach(score => {
      scoresJson = "       {\n" +
                   "           \"ms\": " + score.ms + ",\n" +
                   "           \"name\": \"" + escapeHtml(score.name) + "\"\n" +
                   "       }" :: scoresJson
    })
    
    "{\n" +
    "   \"id\": \"" + setup.id + "\",\n" +
    "   \"name\": \"" + setup.name + "\",\n" +
    "   \"scores\": [\n" +
          scoresJson.mkString(",\n") + "\n" +
    "   ]\n" +
    "}\n"
  }

  def escapeHtml(html:String) = {
    var escaped = html
    escaped = escaped.replace("&", "&amp;")
    escaped = escaped.replace("\"", "&quot;")
    escaped = escaped.replace("<", "&lt;")
    escaped = escaped.replace(">", "&gt;")
    escaped = escaped.replace("'", "&apos;")
    escaped
  }

  def action(a:String) : String = {
    val selectRegex = new Regex("^select_([0-9]+)_([0-9]+)_([0-9]+)$", "x", "y", "z")
    val createGameRegex = new Regex("^creategame_([A-Za-z0-9]+)$", "setupId")
    val addScoreRegex = new Regex("^addscore_(.+)$", "name")
    
    a match {
      case "scramble" => controller.scramble
      case selectRegex(x, y, z) => {
        val tile = controller.findTile(x.toInt, y.toInt, z.toInt)
        controller.selectTile(tile)
      }
      case createGameRegex(setupId) => {
        val setup = controller.setupById(setupId)
        if (setup != null)
          controller.startNewGame(setup)
      }
      case addScoreRegex(name) => {
        if (currentSetup != null) {
          controller.addScore(currentSetup, name, currentMs)
          currentSetup = null
        }
      }
      case "hint" => controller.requestHint
      case "moveables" => controller.requestMoveables
      case _ => // Nothing
    }
    "{ \"done\": true }"
  }
}
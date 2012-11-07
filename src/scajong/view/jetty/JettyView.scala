package scajong.view.jetty

import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.AbstractHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object JettyView {
  def main(args: Array[String]) {
    val view = new JettyView
  }
}

class JettyView extends AbstractHandler {
	val server = new Server(8080)
	server.setHandler(this);
	server.start
	server.join
	
	override def handle(target:String, baseRequest:Request, request:HttpServletRequest, response:HttpServletResponse) {	  
	  response.setContentType("text/html;charset=utf-8")
		response.setStatus(HttpServletResponse.SC_OK)
		baseRequest.setHandled(true)
		val writer = response.getWriter()
		writer.println("<h1>Scajong</h1>")
	}
}
package androidServer;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class postMessage
 */
@WebServlet("/postMessage")
public class postMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public postMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("New user connected");
		if(request.getParameter("message") == null) {
//			response.getWriter().append("");
			request.getParameter("query");
			List<String> ret = Database.getMessages(request.getParameter("latitude"), request.getParameter("longitude"), request.getParameter("distance"), request.getParameter("time"), request.getParameter("type"));
			for(int i = 0; i < ret.size(); i += 4) {
				response.getWriter().append(ret.get(i) + "|");
				response.getWriter().append(ret.get(i+1) + "|");
				response.getWriter().append(ret.get(i+2) + "|");
				response.getWriter().append(ret.get(i+3));
				if(i + 4 < ret.size()) response.getWriter().append("||");
			}
		}
		else { 
//			response.getWriter().append("").append(request.getContextPath());
			request.getParameter("updates");
			System.out.println(request.getParameter("message"));
			Database.postMessage(request.getParameter("message"), request.getParameter("latitude"), request.getParameter("longitude"), request.getParameter("type"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

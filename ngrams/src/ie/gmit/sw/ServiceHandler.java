package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * To compile this servlet, open a command prompt in the web application directory and execute the following commands:
 *
 * Linux/Mac																	Windows
 * ---------																	---------
 * cd WEB-INF/classes/															cd WEB-INF\classes\
 * javac -cp .:$TOMCAT_HOME/lib/servlet-api.jar ie/gmit/sw/*.java				javac -cp .:%TOMCAT_HOME%/lib/servlet-api.jar ie/gmit/sw/*.java
 * cd ../../																	cd ..\..\
 * jar -cf ngrams.war *															jar -cf ngrams.war *
 *
 * Drag and drop the file ngrams.war into the webapps directory of Tomcat to deploy the application. It will then be
 * accessible from http://localhost:8080.
 *
 * NOTE: the text file containing the 253 different languages needs to be placed in /data/wili-2018-Edited.txt. This means
 * that you must have a "data" directory in the root of your file system that contains a file called "wili-2018-Edited.txt".
 * Do NOT submit the wili-2018 text file with your assignment!
 *
*/

/**
* This servlet will be deployed on the tomcat server and is used to run the application. 
* Overall it manages all aspects of the application, from setting up the database and making requests.
*
* @author Matthew Sloyan
*/

public class ServiceHandler extends HttpServlet {
	private String languageDataSet = null; //This variable is shared by all HTTP requests for the servlet
	private static int jobNumber = 0; //The number of the task in the async queue
	private File f;

	/**
	* Gets the servlet and sets up the database on startup of Tomcat server.
	* The server will wait till the database is setup to ensure it's correct.
	* Running time: O(1)
	*
	* @see InitialiseDatabase
	* @throws ServletException if servlet error occurs
	*/
	public void init() throws ServletException {
		ServletContext ctx = getServletContext(); //Get a handle on the application context
		languageDataSet = ctx.getInitParameter("LANGUAGE_DATA_SET"); //Reads the value from the <context-param> in web.xml

		//You can start to build the subject database at this point. The init() method is only ever called once during the life cycle of a servlet
		f = new File(languageDataSet);
		
		new InitialiseDatabase().initialise(languageDataSet);
	}

	/**
	* Handles get requests to the servlet when doPost is called.
	* When a user enters a language query this method is called which adds their
	* job to the in queue. This job is processed by the JobProcessor and is added to the outqueue
	* which is fetched and checked for the current job number every three seconds. 
	* If the job is found the result is displayed to the user.
	* 
	* Multiple requests can be made at the same time from different clients and 
	* all variables are contained within one get request.
	* Running time: O(1)
	* 
	* @param req HttpServletRequest from post.
	* @param resp HttpServletResponse sent back to user (PrintWriter).
	*
	* @see JobProducer
	* @see JobProcessor
	* @throws ServletException if servlet error occurs
	* @throws IOException if output error occurs 
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html"); //Output the MIME type
		PrintWriter out = resp.getWriter(); //Write out text. We can write out binary too and change the MIME type...

		//Initialise some request variables with the submitted form info. These are local to this method and thread safe...
		String option = req.getParameter("cmbOptions"); //Change options to whatever you think adds value to your assignment...
		String query = req.getParameter("query");
		String taskNumber = req.getParameter("frmTaskNumber");
		String result = req.getParameter("result");

		out.print("<html><head><title>Advanced Object Oriented Software Development Assignment</title>");
		out.print("</head>");
		out.print("<body>");
		
		if (taskNumber == null && query != null){
			taskNumber = new String("T" + jobNumber);
			jobNumber++;
			
			//Add job to in-queue
			JobProducer.getInstance().putJobInQueue(new LanguageRequest(query, taskNumber));
		}else{
			//Check out-queue for finished job
			Map<String, String> outQueue = JobProcessor.getInstance().getOutQueueMap();
			
			// If job is found, set results and remove from queue.
			if (outQueue.containsKey(taskNumber)) {
				result = outQueue.get(taskNumber);
				outQueue.remove(taskNumber);
			}
		}
		
		// Display page with wait time or result.
		display(out, option, query, taskNumber, result);
	}

	/**
	* Prints the HTML page to the user on each get request.
	* Running time: O(1)
	* 
	* @param out PrintWriter to print page to user.
	* @param option User selection for future features.
	* @param taskNumber Unique task number for current job.
	* @param result Returned language from query.
	*/
	private void display(PrintWriter out, String option, String query, String taskNumber, String result) {
		
		if (result == null) {
			out.print("<H2>Processing request for Job#: " + taskNumber + " - Estimated wait time of 3 seconds.</H2>");
		}
		else {
			out.print("<H2>Predicted Language: " + result + "</H2>");
		}
		
		out.print("<font color=\"#993333\"><b>");
		//out.print("Language Dataset is located at " + languageDataSet + " and is <b><u>" + f.length() + "</u></b> bytes in size");
		out.print("<br>Option(s): " + option);
		out.print("<br>Query Text : " + query);
		out.print("</font><p/>");
		
		out.print("<button onclick=\"location.href='http://localhost:8080/ngrams/'\" type=\"button\">New Query</button>");
		
		out.print("<form method=\"POST\" name=\"frmRequestDetails\">");
		out.print("<input name=\"cmbOptions\" type=\"hidden\" value=\"" + option + "\">");
		out.print("<input name=\"query\" type=\"hidden\" value=\"" + query + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("<input name=\"result\" type=\"hidden\" value=\"" + result + "\">");
		out.print("</form>");
		out.print("</body>");
		out.print("</html>");

		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 3000);");
		out.print("</script>");
	}

	/**
	* Handles post requests from the client (doProcess)
	* Running time: O(1)
	* 
	* @param req HttpServletRequest from client.
	* @param resp HttpServletResponse sent back to user (PrintWriter from doGet).
	*
	* @throws ServletException if servlet error occurs
	* @throws IOException if output error occurs 
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
}
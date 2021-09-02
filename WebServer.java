import java.net.*;
import java.io.*;

public class WebServer extends Thread {

  protected Socket clientSocket;
  public static final int PORTA = 7000;

  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(PORTA);
      System.out.println ("Connection Socket Created");
      try {
        while (true) {
          System.out.println ("Waiting for Connection");
          new WebServer (serverSocket.accept());
        }
      }
      catch (IOException e) {
        System.err.println("Accept failed.");
        System.exit(1);
      }
    }
    catch (IOException e) {
      System.err.println("Could not listen on port: "+ PORTA);
      System.exit(1);
    }
    finally {
      try {
        serverSocket.close();
      }
      catch (IOException e) {
        System.err.println("Could not close port: "+ PORTA);
        System.exit(1);
      }
    }
  }


  private WebServer (Socket clientSoc) {
    clientSocket = clientSoc;
    start();
  }

  public void run() {

    System.out.println ("New Communication Thread Started");

    try {

      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true);

      String requisicao = in.readLine();
  	  String partes[] = requisicao.split(" ");
      String filePath = partes[1].substring(1);
      System.out.println("arquivo requisitado: " + filePath);

      try{
  		  File f = new File (filePath);
        if (f.exists()) {
          BufferedReader fin = new BufferedReader(new FileReader(f));
  		    out.println("HTTP/1.0 200 OK");
  		    out.println("Content-Type: text/html\n");
  		    String line;
  		    while ((line = fin.readLine()) != null)
              out.println(line);
  		    fin.close();
        } else {
          out.println("HTTP/1.0 404 Not Found");
          out.println("Content-Type: text/html\n");
          out.println("<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>");
        }
      } catch(IOException e) {
        e.printStackTrace();
      }
      in.close();
      out.close();
  	  clientSocket.close();
  			
    } catch (IOException e) {
           System.err.println("Problem with Communication Server");
           System.exit(1);
    }
  }
}

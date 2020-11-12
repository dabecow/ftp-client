package edu.oreluniver.networks.ftpclient.model;

import java.io.*;
import java.net.Socket;

public class FtpClient {

  private Socket socket;
  private BufferedReader in;
  private PrintWriter writer;
  private String log;

  public FtpClient(){
    log = "Log:\n";
  }

  public void setSocket(Socket socket){
    this.socket = socket;
  }

  public void initStreams() throws IOException {
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(socket.getOutputStream(), true);
  }

  public void appendToLogLn(String message){
    this.log += message + "\n";
  }

  public String getLog() {
    return log;
  }

  public BufferedReader getIn() {
    return in;
  }

  public PrintWriter getWriter() {
    return writer;
  }

  public Socket getSocket() {
    return socket;
  }

}

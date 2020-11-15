package edu.oreluniver.networks.ftpclient.model;

import java.io.*;
import java.net.Socket;

public class FtpClient {

  private Socket cmdSocket;
  private BufferedReader in;
  private PrintWriter writer;
  private String log;
  private Socket dataSocket;

  public FtpClient(){
    log = "Log:\n";
  }

  public void setCmdSocket(Socket cmdSocket){
    this.cmdSocket = cmdSocket;
  }

  public void setDataSocket(Socket dataSocket) {
    this.dataSocket = dataSocket;
  }

  public void initDataStreams() throws IOException {
    in = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
    writer = new PrintWriter(dataSocket.getOutputStream(), true);
  }

  public void initCmdStreams() throws IOException {
    in = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
    writer = new PrintWriter(cmdSocket.getOutputStream(), true);
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

  public Socket getCmdSocket() {
    return cmdSocket;
  }

}

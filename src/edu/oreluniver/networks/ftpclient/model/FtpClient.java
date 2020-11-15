package edu.oreluniver.networks.ftpclient.model;

import java.io.*;
import java.net.Socket;

public class FtpClient {

  private Socket cmdSocket;
  private BufferedReader cmdIn;
  private PrintWriter cmdWriter;
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
    cmdIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
    cmdWriter = new PrintWriter(dataSocket.getOutputStream(), true);
  }

  public void initCmdStreams() throws IOException {
    cmdIn = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
    cmdWriter = new PrintWriter(cmdSocket.getOutputStream(), true);
  }

  public void appendToLogLn(String message){
    this.log += message + "\n";
  }

  public String getLog() {
    return log;
  }

  public BufferedReader getCmdIn() {
    return cmdIn;
  }

  public PrintWriter getCmdWriter() {
    return cmdWriter;
  }

  public Socket getCmdSocket() {
    return cmdSocket;
  }

  public Socket getDataSocket() {
    return dataSocket;
  }
}

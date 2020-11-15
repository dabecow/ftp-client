package edu.oreluniver.networks.ftpclient.service;

import edu.oreluniver.networks.ftpclient.model.FtpClient;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class FtpClientService implements IFtpClientService{

  private final FtpClient ftpClient;

  public FtpClientService(FtpClient ftpClient){
      this.ftpClient = ftpClient;
    }

  private void sendMessage(String message){
    ftpClient.getCmdWriter().println(message);
    ftpClient.appendToLogLn("[CLIENT] " + message);
  }

  @Override
  public String receiveAnswer() throws IOException {
    StringBuilder answer = new StringBuilder();

    do {
      int c = ftpClient.getCmdIn().read();
      answer.append((char) c);
    } while(ftpClient.getCmdIn().ready());

    ftpClient.appendToLogLn("[SERVER] " + answer);
    return answer.toString();
  }

  private boolean connectClientDataStreams() throws IOException {
    sendMessage("PASV");

    String answer = receiveAnswer();

    if (!answer.startsWith("227"))
      return false;
    String ip;
    int port;

    int opening = answer.indexOf('(');
    int closing = answer.indexOf(')', opening + 1);
    if (closing > 0) {
      String dataLink = answer.substring(opening + 1, closing);
      StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
      try {
        ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
            + tokenizer.nextToken() + "." + tokenizer.nextToken();
        port = Integer.parseInt(tokenizer.nextToken()) * 256
            + Integer.parseInt(tokenizer.nextToken());
      } catch (Exception e) {
        throw new IOException("Been received bad data link information: "
            + answer);
      }
    } else throw new IOException();

    Socket socket = new Socket(ip, port);
    ftpClient.setDataSocket(socket);
    ftpClient.initDataStreams();

    return true;
  }


  @Override
  public void connectClientCmdStreams(String host, int port) throws IOException {
    ftpClient.setCmdSocket(new Socket(host, port));
    ftpClient.initCmdStreams();
  }

  @Override
  public boolean authorise(String username, String password) throws IOException {
    sendMessage("USER " + username);
    if (!receiveAnswer().startsWith("331"))
      return false;

    sendMessage("PASS " + password);

    return receiveAnswer().startsWith("230");
  }

  @Override
  public void changeWorkDirectory(String newDirectoryPath) {
    sendMessage("CWD " + newDirectoryPath);
  }

  @Override
  public void reinitialize() {
    sendMessage("REIN");
  }

  @Override
  public String quit() throws IOException {
    sendMessage("QUIT");
    ftpClient.getCmdWriter().close();
    ftpClient.getCmdIn().close();
    ftpClient.getCmdSocket().close();
    return ftpClient.getLog();
  }

  @Override
  public String askServerToOpenPort(String ip) throws IOException {
    sendMessage("PORT " + ip);
    return receiveAnswer();
  }

  @Override
  public String makeDirectory(String directoryName) throws IOException {
      sendMessage("MKD " + directoryName);
      return receiveAnswer();
  }

  @Override
  public String removeDirectory(String directoryName) throws IOException {
      sendMessage("RMD " + directoryName);
      return receiveAnswer();
  }

  @Override
  public String getFilesList() throws IOException {
    sendMessage("LIST");
    return receiveAnswer();
  }

  @Override
  public String sendFile(File file) throws IOException {

    if (!connectClientDataStreams() || file.isDirectory())
      throw new IOException("Data streams connect failed or the file is a directory");

    sendMessage("STOR " + file.getName());
    if (!receiveAnswer().startsWith("125"))
      throw new IOException("Failed STOR command");

    byte[] buffer = new byte[4096];
    int bytesRead = 0;

    BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

    Socket dataSocket = ftpClient.getDataSocket();


    BufferedOutputStream dataOutput = new BufferedOutputStream(dataSocket.getOutputStream());

    while ((bytesRead = input.read(buffer)) != -1) {
      dataOutput.write(buffer, 0, bytesRead);
    }

    dataOutput.flush();
    dataOutput.close();
    dataSocket.close();

    return receiveAnswer();
  }

  @Override
  public String getFile() throws IOException {
    return null;
  }

  @Override
  public String renameFile(String oldFileName, String newFileName) throws IOException {
    String answer;
    sendMessage("RNFR " + oldFileName);
    answer = receiveAnswer();

    if (!answer.startsWith("350"))
      return answer;

    sendMessage("RNTO " + newFileName);

    return receiveAnswer();
  }

  @Override
  public String deleteFile(String fileName) throws IOException {
    sendMessage("DELE " + fileName);
    return receiveAnswer();
  }

  @Override
  public String abort() throws IOException {
    sendMessage("ABOR");

    if (!ftpClient.getDataSocket().isClosed())
      ftpClient.getDataSocket().close();

    return receiveAnswer();
  }


}

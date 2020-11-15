package edu.oreluniver.networks.ftpclient.service;

import edu.oreluniver.networks.ftpclient.model.FtpClient;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class FtpClientService implements IFtpClientService{

  private final FtpClient ftpClient;

  public FtpClientService(FtpClient ftpClient){
      this.ftpClient = ftpClient;
    }

  private void sendMessage(String message){
    ftpClient.getWriter().println(message);
    ftpClient.appendToLogLn("[CLIENT] " + message);
  }

  @Override
  public String receiveAnswer() throws IOException {
    StringBuilder answer = new StringBuilder();

    do {
      int c = ftpClient.getIn().read();
      answer.append((char) c);
    } while(ftpClient.getIn().ready());

    ftpClient.appendToLogLn("[SERVER] " + answer);
    return answer.toString();
  }

  private void connectClientDataStreams(String answer) throws IOException {
    sendMessage("PASV");

    answer = receiveAnswer();

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
    ftpClient.getWriter().close();
    ftpClient.getIn().close();
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


}

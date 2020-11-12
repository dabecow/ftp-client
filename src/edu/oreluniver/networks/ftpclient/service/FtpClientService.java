package edu.oreluniver.networks.ftpclient.service;

import edu.oreluniver.networks.ftpclient.model.FtpClient;
import java.io.IOException;
import java.net.Socket;

public class FtpClientService implements IFtpClientService{
    private FtpClient ftpClient;

    public FtpClientService(FtpClient ftpClient){
      this.ftpClient = ftpClient;
    }

  private void sendMessage(String message){
    ftpClient.getWriter().println(message);
    ftpClient.appendToLogLn("[CLIENT] " + message);
  }

  @Override
  public String receiveAnswer() throws IOException {
    String answer = "";

    do {
      int c = ftpClient.getIn().read();
      answer+=(char)c;
    } while(ftpClient.getIn().ready());

    ftpClient.appendToLogLn("[SERVER] " + answer);
    return answer;
  }

  @Override
  public void connectClient(String host, int port) throws IOException {
    ftpClient.setSocket(new Socket(host, port));
    ftpClient.initStreams();
  }

  @Override
  public boolean authorise(String username, String password) throws IOException {
    sendMessage("USER " + username);
    if (receiveAnswer().startsWith("331"))
      return false;

    sendMessage("PASS " + password);

    return receiveAnswer().startsWith("230");
  }



  @Override
  public void changeWorkDirectory(String newDirectoryPath) throws IOException {
    sendMessage("CWD " + newDirectoryPath);
  }

  @Override
  public void reinitialize() throws IOException {
    sendMessage("REIN");
  }

  @Override
  public void quit() throws IOException {
    sendMessage("QUIT");
    ftpClient.getWriter().close();
    ftpClient.getIn().close();
    ftpClient.getSocket().close();
  }
}

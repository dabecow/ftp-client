package edu.oreluniver.networks.ftpclient.service;

import java.io.IOException;

public interface IFtpClientService {

  void connectClient(String host, int port) throws IOException;
  boolean authorise(String username, String password) throws IOException;
  String receiveAnswer() throws IOException;
  void changeWorkDirectory(String newDirectoryPath) throws IOException;
  void reinitialize() throws IOException;
  void quit() throws IOException;

}

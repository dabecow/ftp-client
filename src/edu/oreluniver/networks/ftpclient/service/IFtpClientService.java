package edu.oreluniver.networks.ftpclient.service;

import java.io.IOException;

public interface IFtpClientService {

  void connectClientCmdStreams(String host, int port) throws IOException;
  boolean authorise(String username, String password) throws IOException;
  String receiveAnswer() throws IOException;
  void changeWorkDirectory(String newDirectoryPath) throws IOException;
  void reinitialize() throws IOException;
  String quit() throws IOException;
  String askServerToOpenPort(String ip) throws IOException;

  String makeDirectory(String directoryName) throws IOException;
  String removeDirectory(String directoryName) throws IOException;



}

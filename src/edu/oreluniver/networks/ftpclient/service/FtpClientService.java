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
    } while (ftpClient.getCmdIn().ready());

    ftpClient.appendToLogLn("[SERVER] " + answer);
    return answer.toString();
  }

  private Socket createDataSocket() throws IOException {
    sendMessage("PASV");

    String answer = receiveAnswer();

    if (!answer.startsWith("2"))
      throw new IOException(answer);
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

    return new Socket(ip, port);
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
  public String changeWorkDirectory(String newDirectoryPath) throws IOException {
    sendMessage("CWD " + newDirectoryPath);
    return receiveAnswer();
  }

  @Override
  public String reinitialize() throws IOException {
    sendMessage("REIN");
    return receiveAnswer();
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
  public synchronized String makeDirectory(String directoryName) throws IOException {
      sendMessage("MKD " + directoryName);
      return receiveAnswer();
  }

  @Override
  public String removeDirectory(String directoryName) throws IOException {
      sendMessage("RMD " + directoryName);
      return receiveAnswer();
  }

  @Override
  public String getLog() {
    return ftpClient.getLog();
  }

  @Override
  public synchronized String getFilesList() throws IOException {

    Socket dataSocket = createDataSocket();

    sendMessage("LIST");

    StringBuilder answer = new StringBuilder();

    BufferedReader bInputStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

    do {
      int c = bInputStream.read();
      answer.append((char) c);
    } while(bInputStream.ready());

    dataSocket.close();
//    ftpClient.appendToLogLn("[SERVER] " + answer);

    answer.append(receiveAnswer());
    return answer.toString();
  }

  @Override
  public synchronized String sendFile(File file) throws IOException {

    Socket dataSocket = createDataSocket();

    if (file.isDirectory())
      throw new IOException("The file is a directory");

    sendMessage("STOR " + file.getName());
    if (!receiveAnswer().startsWith("150"))
      throw new IOException("Failed STOR command");

    byte[] buffer = new byte[4096];


    BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

    BufferedOutputStream dataOutput = new BufferedOutputStream(dataSocket.getOutputStream());

    int c;

    while ((c = input.read(buffer)) != -1) {
      dataOutput.write(buffer, 0, c);
    }

    dataOutput.flush();
    dataOutput.close();
    dataSocket.close();

    return receiveAnswer();
  }

  @Override
  public synchronized String getFile(String localPath, String remoteFileName) throws IOException {

    sendMessage("TYPE I");
    String answer;

    if (!(answer = receiveAnswer()).startsWith("200"))
      throw new IOException(answer);

    Socket dataSocket = createDataSocket();




    sendMessage("RETR " + remoteFileName);

    if (!(answer = receiveAnswer()).startsWith("150"))
      throw new IOException("Error - no such file");

    File file = new File(localPath + remoteFileName);
    if (!file.createNewFile())
      throw new IOException("Error when creating the file");

    BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(file));
    BufferedInputStream bufferedInput = new BufferedInputStream(dataSocket.getInputStream());

    byte[] buffer = new byte[4096];

    int counter;
    while ((counter = bufferedInput.read(buffer)) != -1) {
      bufferedOutput.write(buffer, 0, counter);
    }



    bufferedOutput.flush();
    bufferedInput.close();
    bufferedOutput.close();
    dataSocket.close();

    return receiveAnswer();
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

    return receiveAnswer();
  }


}

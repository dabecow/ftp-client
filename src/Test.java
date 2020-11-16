import edu.oreluniver.networks.ftpclient.model.FtpClient;
import edu.oreluniver.networks.ftpclient.service.FtpClientService;
import edu.oreluniver.networks.ftpclient.service.IFtpClientService;

import java.io.File;
import java.io.IOException;

public class Test {

  public static void main(String[] args) {
    FtpClient ftpClient = new FtpClient();

    IFtpClientService iFtpClientService = new FtpClientService(ftpClient);
    try {


    iFtpClientService.connectClientCmdStreams("127.0.0.1", 21);

    iFtpClientService.receiveAnswer();

    iFtpClientService.authorise("test", "228");

      //      Thread thread =
      //          new Thread() {
      //            public void run() {
      //              File file = new File("C:\\Users\\antom\\OneDrive\\Desktop\\ftp-client");
      //              try {
      //                iFtpClientService.sendFile(file);
      //              } catch (IOException e) {
      //                e.printStackTrace();
      //              }
      //              System.out.println("the file is sent");
      //            }
      //          };
      System.out.println(iFtpClientService.getFilesList());
      System.out.println(iFtpClientService.makeDirectory("dir"));
      System.out.println(iFtpClientService.changeWorkDirectory("dir"));

      Thread thread =
              new Thread(() -> {
                File file =
                    new File(
                        "C:\\Users\\antom\\OneDrive\\Desktop\\ftp-client\\Alice Cooper - Love It To Death.flac");
                try {
                  iFtpClientService.sendFile(file);
                  System.out.println("The file is sent");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });

      Thread thread1 =
              new Thread(() -> {
                File file =
                        new File(
                                "C:\\Users\\antom\\OneDrive\\Desktop\\ftp-client\\19 - Comfortably Numb.flac");
                try {
                  iFtpClientService.sendFile(file);
                  System.out.println("The file is sent");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });

      thread.start();
      thread1.start();
//      try {
//        iFtpClientService.sendFile(file);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }

//      System.out.println(iFtpClientService.getFilesList());
      System.out.println(iFtpClientService.makeDirectory("dir1"));

    } catch (Exception e) {
      e.getStackTrace();
      System.out.println("shit happened");

    }


    System.out.println(iFtpClientService.getLog());
  }
}

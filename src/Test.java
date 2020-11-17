import edu.oreluniver.networks.ftpclient.model.FtpClient;
import edu.oreluniver.networks.ftpclient.service.FtpClientService;
import edu.oreluniver.networks.ftpclient.service.IFtpClientService;
import edu.oreluniver.networks.ftpclient.view.consoleView.ClientConsoleView;
import edu.oreluniver.networks.ftpclient.view.consoleView.IClientConsoleView;

public class Test {

  public static void main(String[] args) {
      FtpClient ftpClient = new FtpClient();
      IFtpClientService iFtpClientService = new FtpClientService(ftpClient);
      IClientConsoleView iClientConsoleView = new ClientConsoleView(iFtpClientService);

      boolean isConnected;

      do {
          isConnected = iClientConsoleView.connectToServer();
      } while (!isConnected);

      boolean isAuthorized;

      do {
          isAuthorized = iClientConsoleView.authorise();
      } while (!isAuthorized);

      iClientConsoleView.runMessageInterface();
  }
}

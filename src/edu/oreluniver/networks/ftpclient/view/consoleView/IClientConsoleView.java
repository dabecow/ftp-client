package edu.oreluniver.networks.ftpclient.view.consoleView;

public interface IClientConsoleView {
    boolean connectToServer();
    boolean authorise();
    void runMessageInterface();
}

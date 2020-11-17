package edu.oreluniver.networks.ftpclient.view.consoleView;

import edu.oreluniver.networks.ftpclient.service.IFtpClientService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientConsoleView implements IClientConsoleView {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final IFtpClientService iFtpClientService;
    private final Scanner scanner;


    public ClientConsoleView(IFtpClientService iFtpClientService){
        this.iFtpClientService = iFtpClientService;
        this.scanner = new Scanner(System.in);
    }

    private File chooseFile() throws Exception {
        JFrame frame = new JFrame();

        frame.setVisible(true);

        frame.setExtendedState(JFrame.ICONIFIED);
        frame.setExtendedState(JFrame.NORMAL);

        JFileChooser jFileChooser = new JFileChooser();
        if (jFileChooser.showDialog(null, "Choose file") == JFileChooser.APPROVE_OPTION){
            frame.setVisible(false);
            return jFileChooser.getSelectedFile();
        }
        frame.setVisible(false);
        throw new Exception("File was not chosen");
    }

    private File chooseDirectory() throws Exception {

        JFrame frame = new JFrame();

        frame.setVisible(true);

        frame.setExtendedState(JFrame.ICONIFIED);
        frame.setExtendedState(JFrame.NORMAL);

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jFileChooser.showDialog(null, "Choose destination directory")
                == JFileChooser.APPROVE_OPTION){
            frame.setVisible(false);
            return jFileChooser.getSelectedFile();
        }
        frame.setVisible(false);
        throw new Exception("Directory was not chosen");
    }

    private void showMessageBox(String message, int messageType){
        JFrame frame = new JFrame();

        frame.setVisible(true);

        frame.setExtendedState(JFrame.ICONIFIED);
        frame.setExtendedState(JFrame.NORMAL);
        JOptionPane.showMessageDialog(null, message,"Attention", messageType);
        frame.setVisible(false);

    }

    public boolean connectToServer(){
        String host;

        System.out.println(
            ANSI_CYAN
                + "    ______                ___            __ \n"
                + "   / __/ /_____     _____/ (_)__  ____  / /_\n"
                + "  / /_/ __/ __ \\   / ___/ / / _ \\/ __ \\/ __/\n"
                + " / __/ /_/ /_/ /  / /__/ / /  __/ / / / /_  \n"
                + "/_/  \\__/ .___/   \\___/_/_/\\___/_/ /_/\\__/  \n"
                + "       /_/                                  \n"
                + "\n\nConnect to the FTP server (TSL is not supported).\n");

        System.out.print("Enter host\n>>>");
        host = scanner.nextLine();

        System.out.println("Standard port is 110.");

        try {
            iFtpClientService.connectClientCmdStreams(host, 21);
            System.out.println(ANSI_GREEN + "\nSuccessful connecting to server:\n" + iFtpClientService.receiveAnswer());
            return true;
        } catch (IOException e) {
            System.out.print(ANSI_RED + "\nIO error. Try again.\n" + ANSI_RESET);
            return false;
        }
    }

    public boolean authorise(){
        String username, password;

        System.out.println( ANSI_PURPLE + "You have to authorise to the server");

        System.out.print("Enter your username\n>>>");
        username = scanner.nextLine();

        System.out.print("Enter your password\n>>>");
        password = scanner.nextLine();

        try {
            if (iFtpClientService.authorise(username, password)){
                System.out.println(ANSI_GREEN + "Successful authorisation" + ANSI_RESET);
                return true;
            } else return false;
        } catch (IOException e) {
            return false;
        }
    }

    public void runMessageInterface(){

        while (true){

            try {
                System.out.print(">>>");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "ls" -> System.out.println(iFtpClientService.getFilesList());
                    case "cd" -> {
                        System.out.print("Enter the name of the directory\n>");
                        System.out.println(iFtpClientService.changeWorkDirectory(scanner.nextLine()));
                    }
                    case "mkdir" -> {
                        System.out.print("Enter the name of the directory\n>");
                        System.out.println(iFtpClientService.makeDirectory(scanner.nextLine()));
                    }
                    case "rm" -> {
                        System.out.print("Enter the name of the file\n>");
                        System.out.println(iFtpClientService.deleteFile(scanner.nextLine()));
                    }
                    case "reinit" -> {
                        System.out.print("Enter message's number\n>");
                        System.out.println(iFtpClientService.reinitialize());
                        boolean isAuthorized;
                        do {
                            isAuthorized = authorise();
                        } while (!isAuthorized);
                    }
                    case "send" -> {
                        try {
                            File file = chooseFile();
                            Thread thread = new Thread(() -> {
                                try {
                                    iFtpClientService.sendFile(file);
                                    showMessageBox("The file " + file.getName() + " is sent.",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException e) {
                                    showMessageBox(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                                }
                            });
                            thread.start();
                        } catch (Exception e) {
                            showMessageBox(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    case "get" -> {
                        System.out.print("Enter the file's name\n>");
                        String name = scanner.nextLine();
                        try {
                            File file = chooseDirectory();
                            Thread thread = new Thread(() -> {
                                try {
                                    iFtpClientService.getFile(file.getPath(), name);
                                    showMessageBox("The file " + name + " is downloaded.",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException e) {
                                    showMessageBox(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                                }
                            });
                            thread.start();
                        } catch (Exception e){
                            showMessageBox(e.getMessage(), JOptionPane.ERROR_MESSAGE);

                        }
                    }
                    case "rename" -> {
                        System.out.print("Enter the old name of the file, enter, and the new name\n>");
                        System.out.println(iFtpClientService.renameFile(scanner.nextLine(), scanner.nextLine()));
                    }
                    case "rmdir" -> {
                        System.out.print("Enter the name of the directory\n>");
                        System.out.println(iFtpClientService.removeDirectory(scanner.nextLine()));
                    }
                    case "log" -> System.out.println(iFtpClientService.getLog());
                    case "quit" -> {
                        System.out.println(iFtpClientService.quit());
                        return;
                    }
                    default -> System.out.println(ANSI_RED + "Usage:\nls - get list\ncd - change directory\n" +
                            "mkdir - make new directory\nrm - remove file\nreinit - reinitialize\nsend - to send file\n" +
                            "get - to get file\nrename\nrmdir - remove the directory\nlog - get log" + ANSI_RESET);
                }
            } catch (Exception e) {
                System.out.println(ANSI_RED + "Wrong input, please, try again or authorise again" + ANSI_RESET);
            }
        }
    }
}

package org.example.dagdepolyment;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class HelloController {
    @FXML
    private Label welcomeText;
    public ChoiceBox environment;
    public TextArea dagPath;
    public Label message;



    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @FXML
    public void initialize() {
        // Add items to the ChoiceBox
        environment.getItems().addAll("QA", "Prod");
        environment.setValue("QA");
        message.setText(" ");
    }


    public void upload(){
        System.out.println("uploading");
        String filePath=dagPath.getText().trim();
        System.out.println(filePath);
        String env=environment.getValue().toString();
        Path path = Paths.get(filePath);
        //setting qa env
        String qa_username = System.getenv("qa-username");
        String qa_ip = System.getenv("qa-ip");
        String qa_dagfilepath = System.getenv("qa-dagfilepath");
        String qa_password = System.getenv("qa-password");
        //setting prod env
        String prod_username = System.getenv("prod-username");
        String prod_ip = System.getenv("prod-ip");
        String prod_dagfilepath = System.getenv("prod-dagfilepath");
        String prod_password = System.getenv("prod-password");
        if (Files.exists(path) && !filePath.isEmpty()) {
            System.out.println("Path is valid.");
            message.setText("File Path is valid");

            if(env.equals("QA")){
                String msg=uploadToEnv(qa_username,qa_ip,22,filePath,qa_dagfilepath ,qa_password);
                if(msg.equals("success")){
                    message.setText("Success");
                }else{
                    message.setText("Failue");
                }
            }else{
                String msg=uploadToEnv(prod_username,prod_ip,22,filePath,prod_dagfilepath,prod_password);
                if(msg.equals("success")){
                    message.setText("Success");
                }else{
                    message.setText("Failue");
                }
            }
        }else{
            System.out.println("Path is not valid");
            message.setText("Path is not valid");
        }

    }

    public String uploadToEnv(String username,String remoteHost,int port,String localFilePath,String  remoteFilePath,String password){
        Session session = null;
        ChannelSftp channelSftp = null;
        if(localFilePath==null){
            return "failure";
        }
        try {
            // Setup JSch


            JSch jsch = new JSch();

            // Establish session
            session = jsch.getSession(username, remoteHost, port);
            session.setPassword(password);


            // Configure session
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // Connect
            session.connect();
            System.out.println("Connected successfully");

            // Open SFTP channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            System.out.println("The channel is created ");

            // Upload file
            channelSftp.put(localFilePath, remoteFilePath);
            System.out.println("File uploaded successfully to " + remoteFilePath);
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        } finally {
            // Cleanup
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
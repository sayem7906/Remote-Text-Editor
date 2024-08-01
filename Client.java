package sample;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
enum  LockObject1{
    LOCK_OBJECT;
}
class ClientThreadRecieve extends Thread{
    private Socket socket;
    private TextAreaDemo textAreaDemo;

    public ClientThreadRecieve(Socket socket,TextAreaDemo textAreaDemo) throws IOException {
        this.socket = socket;
        this.textAreaDemo =textAreaDemo;
    }

    @Override
    public void run(){
        synchronized (LockObject1.LOCK_OBJECT) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message;
            while ((message=scanner.nextLine())!=null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(message.compareTo("END")==0)
                    break;
                System.out.println("Server : "+message);
                textAreaDemo.writeText(message);
            }
            TextArea textArea = textAreaDemo.getTextArea();
            String string = textArea.getText();
            System.out.println(string);

        }

    }
}
class ClientThreadSend extends Thread{
    TextAreaDemo textAreaDemo;
    Socket socket;

    public ClientThreadSend(Socket socket,TextAreaDemo textAreaDemo) throws IOException {
        this.socket = socket;
        this.textAreaDemo = textAreaDemo;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (LockObject1.LOCK_OBJECT){
            PrintStream printStream = null;
            try {
                printStream = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message;
            TextArea textArea =textAreaDemo.getTextArea();
            //textArea.getText();
            while ((message = textArea.getText())!=null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                printStream.println(message);

            }
            printStream.close();

        }


    }

}


public class Client {

    public static void main(String[] args) throws Exception {


        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(TextAreaDemo.class);
            }
        }.start();
         TextAreaDemo textAreaDemo = TextAreaDemo.waitForTextAreaDemo(new TextArea());

            Thread.sleep(10);
            //textAreaDemo.writeText("ABC");



        Socket socket = new Socket("127.0.0.1",1055);
        ClientThreadSend clientSend = new ClientThreadSend(socket,textAreaDemo);
        ClientThreadRecieve clientRecieve = new ClientThreadRecieve(socket,textAreaDemo);
        clientSend.start();
        clientRecieve.start();
        clientSend.join();
        clientRecieve.join();
    }

}

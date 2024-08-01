package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static sun.java2d.cmm.ColorTransform.In;
enum  LockObject{
    LOCK_OBJECT;
}
class ServerThreadSend extends Thread {
    Socket socket;
    File file;
    //static volatile boolean running = false;

    public ServerThreadSend(Socket socket) throws IOException {
        this.socket = socket;
    }

    public ServerThreadSend(Socket socket,File file) throws IOException {
        this.socket = socket;
        this.file =file;
    }

    @Override
    public void run() throws NoSuchElementException {
        synchronized (LockObject.LOCK_OBJECT)
        {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner scanner = new Scanner(bufferedReader);

            PrintStream printStream = null;
            try {
                printStream = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean messageSent = false;

            String message=null;
            while ((message=scanner.nextLine())!=null) {
                System.out.println(message);
                printStream.println(message);
                /*if(message.compareTo("END")==0)
                    break;*/

            }
            try {
                fileReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            printStream.close();

        }

    }
}
class ServerThreadAccept extends Thread{
    Socket socket;
    File file;
    public ServerThreadAccept(Socket socket,File file) throws IOException {
        this.socket=socket;
        this.file = file;
    }

    @Override
    public void run(){

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (LockObject.LOCK_OBJECT){
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter;
            Scanner scanner=null;
            try {
                scanner = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while()
            while((message=scanner.nextLine())!=null){
                printWriter = new PrintWriter(bufferedWriter);
                System.out.println(message);
                printWriter.println(message);
                printWriter.close();

            }
            try {
                fileWriter.close();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



            //printWriter.flush();
        }
    }

}
public class Server {
    public Object lock = new Object();
    public  static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("C:\\Users\\ASUS\\Desktop\\abc.txt");
        ServerSocket serverSocket = new ServerSocket(1055);
        Socket socket = serverSocket.accept();
        ServerThreadAccept serverThreadAccept = new ServerThreadAccept(socket,file);
        ServerThreadSend serverThreadSend = new ServerThreadSend(socket,file);
        serverThreadAccept.start();
        serverThreadSend.start();
        serverThreadAccept.join();
        serverThreadSend.join();
    }



}

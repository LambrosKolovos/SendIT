package sample;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class MailServer
{
    public static void main(String[] args) throws IOException
    {
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = new ServerSocket(port);

        ArrayList<Account> registeredAccounts = new ArrayList<>();
        ArrayList<String> takenNames = new ArrayList<>();
        Email welcomeMail = new Email("SendIT Team", "Welcome to SendIt!", "Welcome to sendIt! We hope you like the application.\nAny feedback is always appreciated!");
        Email mail_1 = new Email("user", "Java", "Java is a general-purpose programming language that is class-based, object-oriented, and designed\nto have as few implementation dependencies as possible.");
        Email mail_2 = new Email("admin", "Random", "This is a sample email.");



        while (true)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new ClientHandler(s, dis, dos, registeredAccounts, takenNames, welcomeMail);

                Account admin = new Account("admin", "admin1");
                Account user = new Account("user", "user1");


                admin.addToMailbox(welcomeMail);
                admin.addToMailbox(mail_1);
                admin.addToMailbox(mail_2);
                user.addToMailbox(welcomeMail);

                registeredAccounts.add(admin);
                registeredAccounts.add(user);
                // Invoking the start() method
                t.start();

            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}
package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

// ClientHandler class
class ClientHandler extends Thread
{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private ArrayList<Account> registeredAccounts;
    private ArrayList<String> takenNames;
    private Account userLogged;
    private Email welcomeMail;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, ArrayList<Account> accounts, ArrayList<String> names, Email mail)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

        this.registeredAccounts = accounts;
        this.takenNames = names;

        welcomeMail = mail;
    }

    @Override
    public void run()
    {
        String command;

        while (true)
        {
            try {
                command = dis.readUTF();

                if(command.equals("NEW_ACCOUNT")) {
                    userRegistration();
                }
                if(command.equals("USER_LOG_IN")){
                    userLogin();
                }

                if(command.equals("READ_EMAIL")){
                    readEmail();
                }
                if(command.equals("DELETE_EMAIL")){
                    deleteEmail();
                }
                if(command.equals("CANCEL_EMAIL")){
                    loadMails(userLogged);
                }
                if(command.equals("NEW_EMAIL")){
                    sendNewMail();
                }
                if(command.equals("REFRESH")){
                    loadMails(userLogged);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void userRegistration() throws IOException {
        String username = dis.readUTF();
        String address = dis.readUTF();
        String pwd = dis.readUTF();

        Account acc = new Account(username, pwd);
        acc.addToMailbox(welcomeMail);
        registeredAccounts.add(acc);


    }

    private void userLogin() throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();

        for(Account x : registeredAccounts){
            if(x.getName().toLowerCase().equals(username))
                if(x.getPassword().equals(password)) {
                    dos.writeUTF("SUCCESS");
                    loadMails(x);
                    userLogged = x;
                    return;
                }
        }
        dos.writeUTF("FAILED");
    }

    private void sendNewMail() throws IOException {
        String sender = dis.readUTF();
        String subject = dis.readUTF();
        String receiverName = dis.readUTF();
        String mainBody = dis.readUTF();

        for(Account receiver: registeredAccounts){
            if(receiver.getName().equals(receiverName)){
                Email email = new Email(sender, subject, mainBody);
                receiver.addToMailbox(email);
                dos.writeUTF("USER_FOUND");
                return;
            }
        }

        dos.writeUTF("USER_NOT_FOUND");
    }

    private void loadMails(Account x) throws IOException {
        System.out.println("LOADING EMAILS");
        for(int i=0; i < x.getMailbox().size(); i++){
            dos.writeUTF(x.getMailbox().get(i).getSender());
            dos.writeUTF(x.getMailbox().get(i).getSubject());
            dos.writeUTF(x.getMailbox().get(i).getBody());
            dos.writeUTF(x.getMailbox().get(i).getDate());
            if(x.getMailbox().get(i).isNew())
                dos.writeUTF("NEW");
            else
                dos.writeUTF("SEEN");
        }

        System.out.println("Read emails: " + x.getMailbox().size());
    }

    private void readEmail() throws IOException {
        String emailId = dis.readUTF();
        int id = Integer.parseInt(emailId);
        userLogged.getMailbox().get(id).setNew(false);
    }

    private void deleteEmail() throws IOException {
        String emailID = dis.readUTF();
        int id = Integer.parseInt(emailID);
        userLogged.getMailbox().remove(id);
    }
}

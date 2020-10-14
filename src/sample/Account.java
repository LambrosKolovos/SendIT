package sample;

import java.util.ArrayList;

public class Account {
    String name;
    String password;

    ArrayList<Email> mailbox;

    public Account(String name, String password){
        this.name = name;
        this.password = password;

        mailbox = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Email> getMailbox() {
        return mailbox;
    }

    public void addToMailbox(Email e){
        mailbox.add(e);
    }
}

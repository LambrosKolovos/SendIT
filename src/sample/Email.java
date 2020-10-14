package sample;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Email {

    private String sender;
    private String subject;
    private String body;
    private String date;
    private boolean isNew;

    public Email(String sender, String subject, String body){
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        isNew = true;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d = new Date();
        date =  formatter.format(d);
    }

    public String getDate(){
        return date;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}

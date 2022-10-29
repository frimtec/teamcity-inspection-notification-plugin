package com.github.frimtec.teamcity.plugin.inspectionnotification;

import javax.mail.*;
import java.util.Properties;

public class MailboxLogger {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            throw new IllegalAccessException("Usage: MailboxLogger email_account [password]");
        }

        Properties props = new Properties();

        String host = "localhost";
        int port = 3110;
        String username = args[0];
        String password = args.length > 1 ? args[1] : args[0];
        String provider = "pop3";

        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore(provider);
        store.connect(host, port, username, password);

        Folder inbox = store.getFolder("INBOX");
        if (inbox == null) {
            System.out.println("No INBOX");
            System.exit(1);
        }
        System.out.println("Logging received emails");
        System.out.println("=======================");
        System.out.println("Account: " + username);
        System.out.println();
        try {
            int maxMessage = 0;
            //noinspection InfiniteLoopStatement
            while (true) {
                inbox.open(Folder.READ_WRITE);
                Message[] messages = inbox.getMessages();
                for (int i = maxMessage; i < messages.length; i++) {
                    Message message = messages[i];
                    message.writeTo(System.out);
                    System.out.println("------------------------------------------------------------");
                    maxMessage++;
                }
                inbox.close(false);
                //noinspection BusyWait
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Go down.");
        } finally {
            store.close();
        }
    }
}
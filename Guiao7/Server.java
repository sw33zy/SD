import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;



class ContactList {
    private List<Contact> contacts;


    public ContactList() {
        contacts = new ArrayList<>();

        contacts.add(new Contact("John", 20, 253123321, null, new ArrayList<>(Arrays.asList("john@mail.com"))));
        contacts.add(new Contact("Alice", 30, 253987654, "CompanyInc.", new ArrayList<>(Arrays.asList("alice.personal@mail.com", "alice.business@mail.com"))));
        contacts.add(new Contact("Bob", 40, 253123456, "Comp.Ld", new ArrayList<>(Arrays.asList("bob@mail.com", "bob.work@mail.com"))));
    }

    // @TODO
    public void addContact (Contact contact) throws IOException {
            contacts.add(contact);
    }

    // @TODO
    public void getContacts (DataOutputStream out) throws IOException {
        out.writeInt(contacts.size());
        out.flush();
        for(Contact contact : contacts) {
            contact.serialize(out);
            out.flush();
        }
    }
    
}

class ServerWritterWorker implements Runnable {
    private Socket socket;
    private ContactList contactList;

    public ServerWritterWorker (Socket socket, ContactList contactList) {
        this.contactList = contactList;
        this.socket = socket;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                int option = in.readInt();
                switch (option) {
                    case 1:
                        Contact newContact = Contact.deserialize(in);
                        contactList.addContact(newContact);
                        System.out.println(newContact.toString());
                        break;
                    case 2:
                        contactList.getContacts(out);
                        break;
                    case 0:
                        throw new EOFException();
                }
            }

        } catch (EOFException e) {
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReaderWorker implements Runnable{
    private Socket socket;
    private ContactList contacts;

    public ReaderWorker(Socket socket, ContactList contacts){
        this.socket = socket;
        this.contacts = contacts;
    }

    @Override
    public void run(){
        try{
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            contacts.getContacts(out);

            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ServerReaderWorker implements Runnable{
    private ServerSocket serverSocket;
    private ContactList contactList = new ContactList();

    public ServerReaderWorker(int port, ContactList contactList) throws IOException{
        this.contactList = contactList;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run(){
        while(true){
            Socket socket;
            try{
                socket = serverSocket.accept();
                Thread worker = new Thread(new ReaderWorker(socket, contactList));
                worker.start();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}


public class Server {

    public static void main (String[] args) throws IOException {
        ContactList contactList = new ContactList();
        ServerSocket serverSocket = new ServerSocket(12345);

        while(true){
            Socket socket = serverSocket.accept();
            Thread writter_worker = new Thread(new ServerWritterWorker(socket, contactList));
            writter_worker.start();
            Thread reader_worker = new Thread(new ServerReaderWorker(34567, contactList));
            reader_worker.start();
        }
            
           
    }
    

}
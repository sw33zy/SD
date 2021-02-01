import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

class Register{
    private final ReentrantLock l = new ReentrantLock();
    private int n = 0;
    private int sum = 0;

    public void add(int value) {
        l.lock();
        try{
            sum+=value;
            n++;
        } finally {l.unlock();}
    }

    public double avg(){
        l.lock();
        try{
            if(n<1)
                n=1;
            return (double) sum/n;
        } finally {l.unlock();}
    }
}

class ServerWorker implements Runnable{
    private Register register;
    private Socket socket;

    public ServerWorker(Socket socket, Register register) {
        this.socket = socket;
        this.register = register;
    }

    @Override
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            int sum = 0;
            String line;
            while((line=in.readLine())!=null){
                try{
                    int value = Integer.parseInt(line);
                    sum+=value;
                    register.add(value);
                } catch(NumberFormatException e){
                    //ignora invalidos
                }

                out.print(sum);
                out.flush();
            }

            out.print(register.avg());
            out.flush();
        } finally{};
    }
}

class Server{
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(12345);
        Register register = new Register();

        while(true) {
            Socket socket = ss.accept();
            Thread worker = new Thread(new ServerSumConc(socket,register));
            worker.start();
        }
    }
}
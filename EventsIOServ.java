
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.*;

import java.nio.file.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.stream.*;


public class EventsIOServ{

    public static void main( String[] args ) throws IOException
    {
        ServerSocket s = new ServerSocket(8942);
        Object[] fld;
        System.out.println("Starting");
        while (true) {
                        Socket connectionSocket = s.accept();
                        fld = Files.list(Paths.get("./events")).collect(Collectors.toList()).toArray();
                        System.out.println("connected from " + connectionSocket.getInetAddress().getHostName() + "...");
                        Thread server = new ThreadedServer(connectionSocket, fld);
                        server.start();
                }
  }
}
    
 class ThreadedServer extends Thread {
          Socket s;
          Object[] fld;
          public ThreadedServer(Socket _s, Object[] _fld) {
                  fld = _fld;
                  s = _s;
          }
          
          
          @Override
          public void run() {
                  try {
                        boolean st = true;
                        while(st) {
                                try {
                                        int i = 0;
                                        if (s.isBound()) {
                                                for ( i = 0; i < fld.length; i++) {
                                                        File file = new File(new String(fld[i].toString()));
                                                        System.out.println(new String(fld[i].toString()));
                                                        int dataSize = (int) file.length();
                                                        //System.out.println(dataSize);
                                                        String htb = (Integer.toString(dataSize) + "-"+Integer.toString(fld.length)+"-");
                                                        byte[] heartB = htb.getBytes(StandardCharsets.UTF_8);
                                                        byte[] RheartB = new byte[heartB.length];
                                                        FileInputStream fs = new FileInputStream(file.toString());
                                                        //BufferedInputStream bfis = new BufferedInputStream(fs);
                                                        InputStream is = s.getInputStream();
                                                        OutputStream os = s.getOutputStream();
                                                        byte[] contents = new byte[dataSize];
                                                        fs.read(contents, 0, contents.length);
                                                        //bfis.close();
                                                        fs.close();
                                                        while(true) {
                                                                os.write(heartB, 0, heartB.length);
                                                                is.read(RheartB, 0, RheartB.length);
                                                                String rs = new String(RheartB,StandardCharsets.UTF_8);
                                                                String ss = new String(heartB,StandardCharsets.UTF_8);
                                                                int _rs = Integer.parseInt(rs.split("-")[0]);
                                                                int _ss = Integer.parseInt(ss.split("-")[0]);
                                                                //Thread.sleep(15000);
                                                                if(_rs== _ss) {
                                                                        os.write(contents, 0, contents.length);
                                                                        System.out.println(new String(contents));
                                                                        System.out.println("sent");
                                                                        break;
                
                                                                }
                                                                else if (_rs == 0) {
                                                                	Date dt = new Date();
                                                    				long ts = dt.getTime();
                                                                	FileOutputStream rfos = new FileOutputStream("./events/"+Long.toString(ts)+".json");
                                                                	os.write(RheartB, 0, RheartB.length);
                                                                	is.read(RheartB, 0, RheartB.length);
                                                                	int si = Integer.parseInt(new String(RheartB,StandardCharsets.UTF_8).split("-")[0]);
                                                                	contents = new byte[si];
                                                                	is.read(contents, 0, contents.length);
                                                                	rfos.write(contents, 0, contents.length);
                                                                	rfos.close();
                                                                	
                                                                	
                                                                } else {
                                                             
                                                                System.out.println("not sent");}
                                                                
                                                        }
                                                        //System.out.println(contents[8]);os.write(contents, 0, contents.length);
                                                        //bfis.close();
                                                        fs.close();
                                                }
                                        }
                                        if (i==(fld.length-1)) {
                                                s.close();
                                                st = false;
                                                break;
                                        }
                
                        }catch (Exception e){
                                st = false;
                                System.out.print(e);

                        }
                                }
                }catch (Exception e){
                        System.out.print(e);
                        this.interrupt();
                }
          }
          
  }
  
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Paxos {
    Map<Integer, Node> nodeList;
    private static Integer numNodes = 0;
    Communication comm ; // Object to send messages

    Paxos() throws FileNotFoundException, UnsupportedEncodingException {
        nodeList = new HashMap<>();
        this.comm = Communication.getInstance();
    }
    Node addNode(InetAddress ip, Integer sendPort, Integer recPort) throws IOException {
        Node node = new Node(numNodes); // CHANGE: TODO:
        comm.addNode(numNodes, ip, sendPort, recPort);
        Communication.numNodes++;
        numNodes++;
        return node;
    }
    
    public static void main(String[] args) throws IOException {
        InetAddress localIp = InetAddress.getLocalHost();
        Paxos paxos = new Paxos();
        Scanner inputReader = new Scanner(System.in);
        
        // ServerSocket s1 = new ServerSocket(0);
        // ServerSocket s2 = new ServerSocket(0);
        // Integer p1 =  s1.getLocalPort();
        // Integer p2 =  s2.getLocalPort();
        // s1.close();
        // s2.close();
        String cmdSet = ">> ";
        System.out.println( "Enter the total number of Nodes: " );
        System.out.print(cmdSet);
        int nn = inputReader.nextInt();
        ArrayList<Node> nodeList = new ArrayList<>();
        // int nn = 5;
        for (int i = 0; i < nn; i++) {
            Node n1=paxos.addNode(localIp, 7000+2*i, 7001+2*i);
            nodeList.add(n1);
        }
        for (int i = 0; i < nn; i++) {
            nodeList.get(i).start();
        }

        
        // paxos.comm.send("TIMY", 0, 2);
        // paxos.comm.send("TIMY", 0, 3);
        // paxos.comm.send("TIMY", 0, 4);

        // paxos.comm.send("CMDPREPARE:50", 0, 1);
        // paxos.comm.send("TIMY:", 0, 1);
        // paxos.comm.send("CMDPREPARE:51", 0, 2);
        // paxos.comm.send("CMDPREPARE:52", 0, 3);
        // paxos.comm.send("CMDPREPARE:53", 0, 4);
        // paxos.addNode(localIp, 7080);
        
        String cmd ;
        String[] cmdList ; 
        String mainCmd;

        Integer value,toId,fromId;
        while(true){
            System.out.print(cmdSet);
            cmd = inputReader.nextLine();
            cmdList = cmd.trim().split(" ");
            mainCmd = cmdList[0];

            if (mainCmd.compareTo("exit") == 0){
                System.exit(0);
            }
            else if (mainCmd.compareTo("client") == 0){
                    value = Integer.parseInt(cmdList[1]);
                    while((paxos.comm.getLeader() == -1)){}
                    toId = paxos.comm.getLeader();
                    // toId = Integer.parseInt(cmdList[2]);
                    paxos.comm.send("CMDPREPARE:"+value, toId, toId);
            }
            else if (mainCmd.compareTo("timeout") == 0){
                value = Integer.parseInt(cmdList[1]);
                toId = Integer.parseInt(cmdList[2]);
                paxos.comm.send("TIMY:"+value, toId, toId);
            }else if (mainCmd.compareTo("kill") == 0){
                // value = Integer.parseInt(cmdList[1]);
                toId = Integer.parseInt(cmdList[1]);
                value = 20000;
                // System.out.println(value.toString() + " "+  toId.toString());
                paxos.comm.send("TIMY:"+value, toId, toId);
            }
            else if (mainCmd.compareTo("") == 0){
                continue;
            } 
            else 
            {
                System.out.println(mainCmd + " : Command not Found");
            }
        }
    }
}
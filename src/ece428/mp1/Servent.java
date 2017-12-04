package ece428.mp1;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Servent {
    public static Integer SEND_PORT = 1234;
    public static Integer RECEIVE_PORT = 1235;
    public final NodeID INTRODUCER_NODE;
    protected final Integer MACHINE_NUMBER = Integer.parseInt(new BufferedReader(new FileReader("../number.txt")).readLine());
    protected MembershipList membershipList;
    protected ArrayList<NodeID> heartBeatList;
    protected DatagramSocket socketClient;
    protected DatagramSocket serverSocket;
    protected NodeID self;
    protected PrintStream printStream;

    /**
     * Constructor for the Servent
     *
     * @throws IOException
     */
    public Servent() throws IOException {
        this.printStream = new PrintStream(new FileOutputStream(new File("../output.txt")));
        this.printStream.println("First line!");

        this.membershipList = new MembershipList();
        this.INTRODUCER_NODE = new NodeID(InetAddress.getByName("fa17-cs425-g39-01.cs.illinois.edu"));

        InetAddress inetAddress = null;
        try {
            if (this.MACHINE_NUMBER == 10) {
                inetAddress = InetAddress.getByName("fa17-cs425-g39-" + this.MACHINE_NUMBER.toString() + ".cs.illinois.edu");
            } else {
                inetAddress = InetAddress.getByName("fa17-cs425-g39-0" + this.MACHINE_NUMBER.toString() + ".cs.illinois.edu");
            }
        } catch (final UnknownHostException e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }


        this.self = new NodeID(inetAddress);
        this.membershipList.addNewNode(this.self);
        this.membershipList.addNewNode(this.INTRODUCER_NODE);

        this.serverSocket = new DatagramSocket(
                SEND_PORT,
                inetAddress
        );
    }


    /**
     * Starts the servent.
     */
    public void startServent() throws Exception {
        this.heartBeatList = getKNodes();
        startServer();
        heartBeat();
    }


    /**
     * Starts the "server" part of the servent on a new thread.
     */
    private void startServer() {
        new Thread() {
            @Override
            public synchronized void run() {
                try {
                    while (true) {
                        final byte[] incomingByteStream = new byte[(int) (Math.pow(2, 10) * Servent.this.membershipList.listEntries.size())];
                        final DatagramPacket incomingPacket = new DatagramPacket(
                                incomingByteStream, incomingByteStream.length
                        );

                        // THIS LINE IS BLOCKING
                        // It waits for this machine to receive some packet
                        Servent.this.serverSocket.receive(incomingPacket);
                        retrieveData(incomingPacket);
                    }
                } catch (final IOException e) {
                    System.err.println(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

        }.start();
    }


    /**
     * Retrieves the data from an incoming packet and updates the membership list acordingly.
     *
     * @param incomingPacket - The incoming packet from other servents.
     * @throws IOException
     */
    protected void retrieveData(final DatagramPacket incomingPacket) throws IOException {
        final String data = new String(incomingPacket.getData());
        final MembershipList other = new ObjectSerialization(data).getMembershipList();

        final MembershipListEntry selfInOther = other.listEntries.get(this.self);
        final MembershipListEntry selfInMembershipList = this.membershipList.listEntries.get(this.self);


        if (selfInOther != null && selfInOther.getLocalTime() < 0) {
            selfInMembershipList.setHeartBeatCounter(selfInOther.getHeartBeatCounter());
        }

//        other.listEntries.remove(this.self);
        this.membershipList.updateEntries(other);
        this.printStream.println(other.toString());
//        System.out.println(other.toString());
        selfInMembershipList.updateLocalTime();
//        System.out.println("Length: " + incomingPacket.getData().length);
    }


    /**
     * This is where we send heartbeats to K random nodes.
     */
    private void heartBeat() {
        new Thread() {
            @Override
            public synchronized void run() {
                try {
                    while (true) {
                        Servent.this.membershipList.incrementHeartBeatCount(Servent.this.self);
                        Servent.this.heartBeatList = getKNodes();

                        for (final NodeID nodeID : Servent.this.heartBeatList) {
                            heartBeat(nodeID);
                        }
                        Thread.sleep(500);
                    }
                } catch (final InterruptedException e) {
                    System.out.println(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * We select K random nodes EVERY time we run this function. However, we also check to make sure
     * that the node is alive before we mark it as one of our K nodes.
     */
    protected ArrayList<NodeID> getKNodes() {
        final ArrayList<NodeID> allKeys = new ArrayList<NodeID>(this.membershipList.listEntries.keySet());
        allKeys.remove(this.self);
        if (allKeys.size() <= 5) {
            return allKeys;
        }
        final ArrayList<NodeID> returnList = new ArrayList<NodeID>();
        final Random rand = new Random();
        allKeys.remove(this.INTRODUCER_NODE);
        NodeID node;
        for (int i = 0; i < 4; i++) {
            while (allKeys.size() > 0) {
                node = allKeys.remove(rand.nextInt(allKeys.size()));
                if (this.membershipList.listEntries.get(node).getAlive()) {
                    returnList.add(node);
                    break;

                }
            }
        }
        returnList.add(this.INTRODUCER_NODE);
        return returnList;
    }


    /**
     * This sends a heartbeat to ONE node, which is passed in. Wrapped into a function for easier debugging.
     *
     * @param nodeID
     */
    private void heartBeat(final NodeID nodeID) {
        try {
            Servent.this.socketClient = new DatagramSocket(
                    RECEIVE_PORT,
                    this.self.getIPAddress()
            );

            final byte[] data = new ObjectSerialization(Servent.this.membershipList).toString().getBytes();
            final DatagramPacket sendPacket = new DatagramPacket(
                    data, data.length,
                    nodeID.getIPAddress(),
                    SEND_PORT
            );
            Servent.this.socketClient.send(sendPacket);

            Servent.this.socketClient.close();
        } catch (final IOException e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}

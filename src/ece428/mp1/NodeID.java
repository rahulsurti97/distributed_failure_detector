package ece428.mp1;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NodeID {
    private final long startTime;
    private final InetAddress IPAddress;


    public NodeID(final InetAddress IPAddress) {
        this.startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.IPAddress = IPAddress;
    }

    public NodeID(final InetAddress IPAddress, final long startTime) {
        this.startTime = startTime;
        this.IPAddress = IPAddress;
    }


    /**
     * Have to override equals in order for hashing to work.
     *
     * @param obj - The object we are comparing against.
     * @return Boolean indicating whether or not the objects are the same.
     */
    @Override
    public synchronized boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeID)) {
            return false;
        }
        final NodeID other = (NodeID) obj;
        return this.IPAddress.getHostName().equals(other.IPAddress.getHostName());
    }


    /**
     * Have to override the hashcode to hash objects into our membership list.
     * We hash solely on the IP address (which we figure ensures uniqueness anyway)
     *
     * @return - Hashed value for the IPAddress member variable.
     */
    @Override
    public synchronized int hashCode() {
//        System.out.println(this.IPAddress.getHostName().hashCode() + "\n\n");
        return this.IPAddress.getHostName().hashCode();
    }


    /**
     * Gets the start time for this node.
     *
     * @return - The start time.
     */
    public long getStartTime() {
        return this.startTime;
    }

//    public void setStartTime(final long startTime) {
//        this.startTime = startTime;
//    }


    /**
     * Gets the IP Address that identifies this node.
     *
     * @return - IP Address.
     */
    public InetAddress getIPAddress() {
        return this.IPAddress;
    }

//    public void setIPAddress(final InetAddress IPAddress) {
//        this.IPAddress = IPAddress;
//    }


    /**
     * Overrides toString for debugging.
     *
     * @return - Stringified version of this object.
     */
    @Override
    public String toString() {
        return this.startTime + ":" + new String(this.getIPAddress().getAddress());
    }

}

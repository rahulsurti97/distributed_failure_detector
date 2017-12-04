package ece428.mp1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipList {


    ConcurrentHashMap<NodeID, MembershipListEntry> listEntries;

    public MembershipList() {
        this.listEntries = new ConcurrentHashMap<NodeID, MembershipListEntry>();
    }

    public MembershipList(final ConcurrentHashMap<NodeID, MembershipListEntry> listEntries) {
        this.listEntries = listEntries;
    }

    /**
     * Gets the current time in milliseconds.
     *
     * @return - Current time.
     */
    public static long getCurrentTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * Adds a node into the membership list.
     *
     * @param nodeID - The node we want to add into the membership list.
     */
    public void addNewNode(final NodeID nodeID) {
        this.listEntries.put(
                nodeID,
                new MembershipListEntry()
        );
    }

    /**
     * Adds a node into the membership list.
     *
     * @param nodeID           - The node we want to add into the membership list.
     * @param heartBeatCounter - The heartbeat counter we want to set when we add the node.
     */
    public void addNewNode(final NodeID nodeID, final int heartBeatCounter) {
        this.listEntries.put(
                nodeID,
                new MembershipListEntry(heartBeatCounter)
        );
    }

    /**
     * Pretty printing for debugging.
     *
     * @return Pretty printing of the string.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final NodeID nodeID : this.listEntries.keySet()) {
            final MembershipListEntry curr = this.listEntries.get(nodeID);
            sb
                    .append("\n")
                    .append("Key: ")
                    .append(nodeID.getIPAddress().getHostName()).append(" | ")
                    .append("Value: ")
                    .append(curr.getHeartBeatCounter()).append(", ")
                    .append(curr.getLocalTime()).append(", ")
                    .append(curr.getAlive());
        }
        return sb.toString();
    }

    /**
     * This is the merge function for a membership list. It merges the two membership lists and updates
     * the list in accordance to the gossiping algorithm.
     *
     * @param other A different node's membership list.
     */
    public void updateEntries(final MembershipList other) throws IOException {
        final Iterator it = other.listEntries.entrySet().iterator();
        while (it.hasNext()) {
            final ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry) it.next();
            final NodeID otherKey = (NodeID) pair.getKey();
            final MembershipListEntry otherEntry = other.listEntries.get(otherKey);
            final MembershipListEntry thisEntry = this.listEntries.get(otherKey);
            if (thisEntry != null) {
                final int otherHeartBeatCount = otherEntry.getHeartBeatCounter();
                final int thisHeartBeatCount = thisEntry.getHeartBeatCounter();
                if (otherHeartBeatCount < 4 && thisEntry.getLocalTime() < 0) {
                    new PrintStream(new FileOutputStream(new File("../output.txt"))).println(("NODE REJOIN!\n"));
                    this.addNewNode(otherKey, 0);
                }
                if (otherHeartBeatCount > thisHeartBeatCount) {
                    thisEntry.setHeartBeatCounter(otherHeartBeatCount);
                    thisEntry.updateLocalTime();
                }
            } else if (otherEntry.getAlive()) {
                this.addNewNode(otherKey, otherEntry.getHeartBeatCounter());
            }
        }

        final Iterator i = this.listEntries.entrySet().iterator();
        while (i.hasNext()) {
            final ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry) i.next();
            final NodeID otherKey = (NodeID) pair.getKey();
            final MembershipListEntry thisEntry = this.listEntries.get(otherKey);
            if (getCurrentTime() - thisEntry.getLocalTime() > 3000) {
                if (thisEntry.getAlive()) {
                    new PrintStream(new FileOutputStream(new File("../output.txt"))).println(("NODE DIED!\n"));
                }
                thisEntry.setAlive(false);
                thisEntry.setLocalTime(-1);
            } else {
                thisEntry.setAlive(true);
            }
        }
    }

    /**
     * Increments a node's heartbeat counter.
     *
     * @param nodeID - A node in the network.
     */
    public void incrementHeartBeatCount(final NodeID nodeID) {
        final MembershipListEntry entry = this.listEntries.get(nodeID);
        if (entry != null) {
            entry.setHeartBeatCounter(entry.getHeartBeatCounter() + 1);
            this.listEntries.put(nodeID, entry);
        }
    }
}

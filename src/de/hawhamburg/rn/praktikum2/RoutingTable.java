package de.hawhamburg.rn.praktikum2;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutingTable {

  private final List<TableEntry> table = new ArrayList<>();
  //protected List<Inet4Address> neighbors = new ArrayList<>();

  public RoutingTable(Inet4Address myIP) {
    TableEntry entry = new TableEntry(myIP, myIP, 0);
  }

  /**
   * Update the routing table.
   *
   * @param routingMap map containing all the routing table data of the neighbor peer
   * @param neighbor   address of the neighbor peer that sent the received package
   * @return true if routing table was updated
   */
  public boolean updateTable(Map<Inet4Address, Byte> routingMap, Inet4Address neighbor) {
    boolean isUpdated = false;
    for (Map.Entry<Inet4Address, Byte> entry : routingMap.entrySet()) {
      if (contains(entry.getKey())) {
        if (entry.getValue() + 1 < getEntryByDestIP(entry.getKey()).hopCount) { // if new hop count for existing destIP is smaller
          isUpdated = true;
          getEntryByDestIP(entry.getKey()).neighbor = neighbor;
          getEntryByDestIP(entry.getKey()).hopCount = entry.getValue() + 1;
        }
      } else {
        isUpdated = true;
        table.add(new TableEntry(entry.getKey(), neighbor, entry.getValue() + 1));
      }
    }
    return isUpdated;
  }

  /**
   * Removes entries from the routing table. Entries that get removed are either the specified target
   * or a peer that has the specified target as their next hop.
   *
   * @param destIP target peer
   */
  public void removeFromTable(Inet4Address destIP) {
    // remove target peer entry
    table.remove(getEntryByDestIP(destIP));
    // remove all entries that have destIP as their next hop
    for (TableEntry entry : getEntriesByNeighbor(destIP)) {
      table.remove(entry);
    }
  }

  public boolean contains(Inet4Address destIP) {
    List<Inet4Address> result = new ArrayList<>();
    for (TableEntry entry : table) {
      result.add(entry.destIP);
    }
    return result.contains(destIP);
  }

  public List<TableEntry> getEntries() {
    return table;
  }

  public TableEntry getEntryByDestIP(Inet4Address destIP) {
    List<Inet4Address> temp = new ArrayList<>();
    for (TableEntry entry : table) {
      temp.add(entry.destIP);
    }
    return table.get(temp.indexOf(destIP));
  }

  public List<TableEntry> getEntriesByNeighbor(Inet4Address neighborIP) {
    List<TableEntry> result = new ArrayList<>();
    for (TableEntry entry : table) {
      if (entry.neighbor == neighborIP) {
        result.add(entry);
      }
    }
    return result;
  }

  public static class TableEntry {
    Inet4Address destIP; // destination IP
    Inet4Address neighbor; // neighbor for next hop
    int hopCount; // metric

    public TableEntry(Inet4Address destIP, Inet4Address neighbor, int hopCount) {
      this.destIP = destIP;
      this.neighbor = neighbor;
      this.hopCount = hopCount;
    }
  }
}

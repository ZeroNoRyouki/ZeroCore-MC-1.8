package zero.mods.zerocore.common.inventory;

public interface IPersistInventoryClient {

    /**
     * Connect the inventory-client to the persistent inventory
     *
     * @param inventory the persistent inventory
     * @param firstSlotIndex the index of the first slot reserved for the client
     * @param reservedSlotsCount the number of slots reserved for this client
     */
    void connectToInventory(IPersistInventory inventory, int firstSlotIndex, int reservedSlotsCount);
}

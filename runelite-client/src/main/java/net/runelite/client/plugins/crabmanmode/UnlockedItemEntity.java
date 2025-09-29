package net.runelite.client.plugins.crabmanmode;

import java.time.OffsetDateTime;

public class UnlockedItemEntity {
    private Integer itemId;
    private String itemName;
    private String acquiredBy;
    private OffsetDateTime acquiredOn;

    public UnlockedItemEntity(String itemName, Integer itemId, String acquiredBy, OffsetDateTime acquiredOn) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.acquiredBy = acquiredBy;
        this.acquiredOn = acquiredOn;
    }

    public UnlockedItemEntity(String itemName, Integer itemId, String acquiredBy) {
        this(itemName, itemId, acquiredBy, OffsetDateTime.now());
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getAcquiredBy() {
        return acquiredBy;
    }

    public String setAcquiredBy(String acquiredBy) {
        return this.acquiredBy = acquiredBy;
    }

    public String getItemName() {
        return itemName;
    }

    public OffsetDateTime getAcquiredOn() {
        return acquiredOn;
    }
}
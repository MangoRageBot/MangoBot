package org.mangorage.mangobot.commands.trick;

import com.google.gson.annotations.Expose;
import org.mangorage.commonutils.data.FileName;
import org.mangorage.commonutils.data.IFileNameResolver;
import org.mangorage.mangobot.commands.trick.lua.MemoryBank;

import java.util.HashMap;

public class Trick implements IFileNameResolver {
    @Expose
    private long ownerID;

    @Expose
    private long lastUserEdited;

    @Expose
    private boolean locked = false;

    @Expose
    private long lastEdited;

    @Expose
    private long timesUsed = 0;

    // Cant be used for Script Based Tricks
    @Expose
    private String content;

    @Expose
    private boolean suppress = false;

    // Used for Script Based Tricks
    @Expose
    private String script;

    // Used for Alias Based Tricks
    @Expose
    private String aliasTarget;

    @Expose
    private final String trickID;

    @Expose
    private final long guildID;

    @Expose
    private final long created;

    @Expose
    private TrickType type;

    @Expose
    private MemoryBank memoryBank;

    protected Trick(String trickID, long guildID) {
        this.trickID = trickID;
        this.guildID = guildID;
        this.created = System.currentTimeMillis();
        if (getType() == TrickType.SCRIPT)
            memoryBank = new MemoryBank(new HashMap<>());
    }

    protected void setAliasTarget(String target) {
        this.aliasTarget = target;
    }
    protected void setSuppress(boolean suppress) {
        this.suppress = suppress;
    }

    protected void setType(TrickType type) {
        this.type = type;
    }

    protected void setContent(String content) {
        this.content = content;
    }

    protected void setScript(String script) {
        this.script = script;
    }

    protected void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    protected void setLastUserEdited(long user) {
        this.lastUserEdited = user;
    }

    protected void setLock(boolean locked) {
        this.locked = locked;
    }

    protected void setLastEdited(long ms) {
        this.lastEdited = ms;
    }

    public TrickType getType() {
        return type;
    }

    public String getAliasTarget() {
        return aliasTarget;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public long getLastUserEdited() {
        return lastUserEdited;
    }

    public long getLastEdited() {
        return lastEdited;
    }

    public long getTimesUsed() {
        return timesUsed;
    }

    public String getContent() {
        return content;
    }

    public String getScript() {
        return script;
    }

    public String getTrickID() {
        return trickID;
    }

    public long getCreated() {
        return created;
    }

    public long getGuildID() {
        return guildID;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isSuppressed() {
        return suppress;
    }

    public MemoryBank getMemoryBank() {
        if (memoryBank == null)
            memoryBank = new MemoryBank(new HashMap<>());
        return memoryBank;
    }

    protected void use() {
        timesUsed++;
    }

    @Override
    public FileName resolve() {
        return new FileName(guildID + "", trickID);
    }
}

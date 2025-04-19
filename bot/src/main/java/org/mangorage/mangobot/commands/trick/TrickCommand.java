package org.mangorage.mangobot.commands.trick;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

//import org.mangorage.basicutils.LogHelper;
import org.mangorage.commonutils.data.DataHandler;
import org.mangorage.commonutils.jda.MessageSettings;
import org.mangorage.commonutils.jda.slash.command.Command;
import org.mangorage.commonutils.jda.slash.command.CommandOption;
import org.mangorage.commonutils.misc.Arguments;
import org.mangorage.commonutils.misc.PagedList;
//import org.mangorage.jdautils.command.Command;
//import org.mangorage.jdautils.command.CommandOption;
import org.mangorage.commonutils.misc.RunnableTask;
import org.mangorage.commonutils.misc.TaskScheduler;
import org.mangorage.mangobot.MangoBot;
//import org.mangorage.mangobot.modules.actions.TrashButtonAction;
//import org.mangorage.mangobotapi.core.commands.CommandAlias;
//import org.mangorage.mangobotapi.core.commands.CommandResult;

//import org.mangorage.mangobotapi.core.events.BasicCommandEvent;
//import org.mangorage.mangobotapi.core.events.DiscordEvent;
//import org.mangorage.mangobotapi.core.events.LoadEvent;
//import org.mangorage.mangobotapi.core.events.SaveEvent;

import org.mangorage.mangobot.actions.TrashButtonAction;
import org.mangorage.mangobotcore.jda.command.api.CommandResult;
import org.mangorage.mangobotcore.jda.command.api.ICommand;
import org.mangorage.mangobotcore.jda.event.CommandEvent;
import org.mangorage.mangobotcore.jda.event.DiscordButtonInteractEvent;
import org.mangorage.mangobotcore.jda.event.DiscordModalInteractionEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TrickCommand implements ICommand {
    private static final boolean ALLOW_SCRIPT_TRICKS = true;
    private final MangoBot plugin;

    public static final DataHandler<Trick> TRICK_DATA_HANDLER = DataHandler.create()
            .path("data/tricksV2")
            .maxDepth(3)
            .build(Trick.class);

    private final Map<TrickKey, Trick> TRICKS = new HashMap<>();
    private final Map<String, PagedList<String>> PAGES = new ConcurrentHashMap<>();

    private final TrickScriptable SCRIPT_RUNNER;


    public Trick getTrick(String trickId, long guildId) {
        return TRICKS.get(new TrickKey(trickId, guildId));
    }

    public Collection<Long> getGuilds() {
        Set<Long> set = new HashSet<>();
        TRICKS.forEach((t, k) -> set.add(t.guildId()));
        return set;
    }

    public List<Trick> getTricksForGuild(long guildId) {
        return TRICKS.entrySet()
                .stream()
                .filter(e -> e.getKey().guildId() == guildId)
                .map(Map.Entry::getValue)
                .toList();
    }


    public TrickCommand(MangoBot plugin) {
        this.plugin = plugin;
        this.SCRIPT_RUNNER = new TrickScriptable(plugin);

        DiscordButtonInteractEvent.BUS.addListener(this::onButton);
        DiscordModalInteractionEvent.BUS.addListener(this::onModal);
        CommandEvent.BUS.addListener(this::onCommandEvent);

        TRICK_DATA_HANDLER.load(plugin.getPluginDirectory()).forEach(data -> {
            TRICKS.put(new TrickKey(data.getTrickID(), data.getGuildID()), data);
        });

//        plugin.getPluginBus().addListener(10, LoadEvent.class, this::onLoadEvent);
//        plugin.getPluginBus().addListener(10, SaveEvent.class, this::onSaveEvent);

        Command.slash("trick", "The Trick System")
                .addSubCommand("execute", "Execute a trick!")
                    .executes(e -> {
                        var valueOption = e.getInteraction().getOption("name");
                        if (valueOption != null) {
                            var g = e.getGuild();
                            if (g != null) {
                                var gid = g.getIdLong();
                                var trick = getTrick(valueOption.getAsString(), gid);
                                if (trick != null) {
                                    useTrick(trick, null, e.getChannel(), gid, Arguments.of());
                                    e.reply("Executed Trick!").setEphemeral(true).queue();
                                    return;
                                }

                            }
                        }
                        e.reply("Failed to execute trick!").setEphemeral(true).queue();
                    })
                    .addOption(
                            new CommandOption(OptionType.STRING, "name", "desc", false, true)
                                    .onAutoComplete(e -> {
                                        var guild = e.getGuild();
                                        if (guild != null) {
                                            var id = guild.getIdLong();
                                            var entries = getTricksForGuild(id);
                                            if (entries != null) {
                                                e.replyChoiceStrings(
                                                        entries.stream()
                                                                .filter(k -> k.getType() == TrickType.NORMAL)
                                                                .map(Trick::getTrickID)
                                                                .limit(25)
                                                                .toList()
                                                ).queue();
                                            }
                                        }
                                    })
                    )
                    .build()
                .addSubCommand("create", "Creates a new Trick!")
                    .executes(e -> {

                        var typeOption = e.getOption("type");
                        var type = typeOption != null ? typeOption.getAsString(): "";

                        if (type.equals("NORMAL") || type.equals("SUPPRESSED-NORMAL")) {
                            e.replyModal(
                                    Modal.create("trickcreation" + (type.equals("NORMAL") ? 0 : 1), "Create a new %s Trick!".formatted(type.toLowerCase()))
                                            .addComponents(
                                                    ActionRow.of(
                                                            TextInput.create("trickid", "Trick ID", TextInputStyle.SHORT)
                                                                    .setRequired(true)
                                                                    .build()
                                                    ),
                                                    ActionRow.of(
                                                            TextInput.create("content", "Content", TextInputStyle.PARAGRAPH)
                                                                    .setRequired(true)
                                                                    .setMaxLength(1900)
                                                                    .build()
                                                    )
                                            )
                                            .build()
                            ).queue();
                        } else {
                            e.replyModal(
                                    Modal.create("trickcreation2", "Create a new %s Trick!".formatted(type.toLowerCase()))
                                            .addComponents(
                                                    ActionRow.of(
                                                            TextInput.create("trickid", "Trick ID", TextInputStyle.SHORT)
                                                                    .setRequired(true)
                                                                    .build()
                                                    ),
                                                    ActionRow.of(
                                                            TextInput.create("alias", "Alias", TextInputStyle.SHORT)
                                                                    .setRequired(true)
                                                                    .setMaxLength(30)
                                                                    .build()
                                                    )
                                            )
                                            .build()
                            ).queue();
                        }
                    })
                    .addOption(
                            new CommandOption(OptionType.STRING, "type", "What type of Trick?", true, true)
                                    .onAutoComplete(e -> {
                                        e.replyChoiceStrings(
                                                "NORMAL",
                                                "SUPPRESSED-NORMAL",
                                                "ALIAS"
                                        ).queue();
                                    })
                    )
                    .build()
                .buildAndRegister();
    }

    public void onModal(DiscordModalInteractionEvent event) {
        var DEvent = event.getDiscordEvent();
        var guild = DEvent.getGuild();
        var user = DEvent.getUser();
        if (guild == null) {
            DEvent.deferReply(true).setContent("""
                    Cannot Add Trick. Only works in guilds.
                    """).queue();
        } else {
            var modalID = DEvent.getModalId();
            if (modalID.equals("trickcreation")) {
                var trickID = DEvent.getInteraction().getValue("trickid");
                var content = DEvent.getInteraction().getValue("content");
                if (trickID != null && content != null) {
                    if (exists(trickID.getAsString(), guild.getIdLong())) {
                        DEvent.reply("Cannot create trick '%s' already exists!".formatted(trickID.getAsString())).queue();
                    } else {
                        Trick newTrick = new Trick(trickID.getAsString(), guild.getIdLong());
                        newTrick.setLastUserEdited(user.getIdLong());
                        newTrick.setOwnerID(user.getIdLong());
                        newTrick.setType(TrickType.NORMAL);
                        newTrick.setContent(content.getAsString());
                        save(newTrick);
                        TRICKS.put(new TrickKey(trickID.getAsString(), guild.getIdLong()), newTrick);
                        DEvent.reply("Created new trick '%s'!".formatted(trickID.getAsString())).queue();
                    }
                }
            }
        }
    }
//
//
//    public void onLoadEvent(LoadEvent event) {
//        LogHelper.info("Loading Tricks Data!");
//        TRICK_DATA_HANDLER.load(plugin.getPluginDirectory()).forEach(data -> {
//            TRICKS.put(new TrickKey(data.getTrickID(), data.getGuildID()), data);
//        });
//        LogHelper.info("Finished loading Tricks Data!");
//    }
//
//    public void onSaveEvent(SaveEvent event) {
//        LogHelper.info("Saving Tricks Data!");
//
//        TRICKS.forEach((k, v) -> {
//            save(v);
//        });
//    }
//
    public void onCommandEvent(CommandEvent event) {
        if (!event.isHandled()) {
            Message message = event.getMessage();
            if (!message.isFromGuild()) return;
            long guildID = message.getGuild().getIdLong();
            String command = event.getCommand().toLowerCase();
            String args = event.getArguments().getFrom(0);

            // We have found something that works, make sure we do this so that "Invalid Command" doesn't occur
            // event.setHandled() insures that the command has been handled!
            if (exists(command, guildID))
                event.setHandled(execute(message, Arguments.of("-s", command, args)));
        }
    }


    private void delete(Trick trick) {
        TRICK_DATA_HANDLER.delete(plugin.getPluginDirectory(), trick);
    }

    private void save(Trick trick) {
        TRICK_DATA_HANDLER.save(plugin.getPluginDirectory(), trick);
    }

    private boolean exists(String trickID, long guildID) {
        return getTrick(trickID, guildID) != null;
    }

    private boolean isOwnerAndUnlocked(Trick trick, Member member) {
        if (!trick.isLocked())
            return true;
        return trick.getOwnerID() == member.getIdLong();
    }

    @Override
    public CommandResult execute(Message message, Arguments args) {
        MessageSettings dMessage = plugin.getMessageSettings();
        Member member = message.getMember();
        if (member == null)
            return CommandResult.PASS;
        long guildID = message.getGuild().getIdLong();
        String typeString = args.get(0);
        TrickCMDType type = typeString == null ? TrickCMDType.NONE : TrickCMDType.getType(typeString);
        String trickID = args.get(1);
        if (trickID != null)
            trickID = trickID.toLowerCase();
        boolean suppress = args.hasArg("-suppress");

        if (type == TrickCMDType.NONE) {
            return CommandResult.PASS;
        }

        // By Default Tricks are for Guilds.
        // Will update for Users...
        if (type == TrickCMDType.ADD) {
            if (exists(trickID, guildID)) {
                dMessage.apply(message.reply("Trick '%s' Already Exists!".formatted(trickID))).queue();
                return CommandResult.PASS;
            }

            var trick = new Trick(trickID, guildID);
            if (args.hasArg("-content")) {
                trick.setType(TrickType.NORMAL);
                trick.setSuppress(suppress);
                var content = args.getFrom(args.getArgIndex("-content") + 1);
                if (content == null || content.isEmpty()) {
                    dMessage.apply(message.reply("Content Cannot be null/empty!")).queue();
                    return CommandResult.PASS;
                }
                trick.setContent(content);
            } else if (args.hasArg("-script")) {
                trick.setType(TrickType.SCRIPT);
                // Update for CodeBlocks ->
                var script = args.getFrom(args.getArgIndex("-script") + 1);
                if (script == null || script.isEmpty()) {
                    dMessage.apply(message.reply("Script Cannot be null/empty!")).queue();
                    return CommandResult.PASS;
                }
                trick.setScript(script);
            } else if (args.hasArg("-alias")) {
                // Check if Target is Normal/Scriptable too! Can be added later...

                trick.setType(TrickType.ALIAS);
                // Update for CodeBlocks ->
                var alias = args.getFrom(args.getArgIndex("-alias") + 1);
                if (alias == null || alias.isEmpty()) {
                    dMessage.apply(message.reply("Trick Cannot be null/empty!")).queue();
                    return CommandResult.PASS;
                }

                trick.setAliasTarget(alias);
            }

            trick.setOwnerID(member.getIdLong());
            trick.setLastUserEdited(member.getIdLong());
            trick.setLastEdited(System.currentTimeMillis());

            TRICKS.put(new TrickKey(trickID, guildID), trick);
            save(trick);

            dMessage.apply(message.reply("Added New Trick '%s'!".formatted(trickID))).queue();

        } else if (type == TrickCMDType.MODIFY) {
            if (exists(trickID, guildID)) {
                var trick = getTrick(trickID, guildID);

                if (!isOwnerAndUnlocked(trick, member)) {
                    dMessage.apply(message.reply("Cannot modify/remove Trick '%s' as your not the owner of this trick and its locked.".formatted(trickID))).queue();
                    return CommandResult.PASS;
                }

                if (args.hasArg("-content")) {
                    trick.setType(TrickType.NORMAL);
                    trick.setSuppress(suppress);
                    var content = args.getFrom(args.getArgIndex("-content") + 1);
                    if (content == null || content.isEmpty()) {
                        dMessage.apply(message.reply("Content Cannot be null/empty!")).queue();
                        return CommandResult.PASS;
                    }
                    trick.setContent(content);
                } else if (args.hasArg("-script")) {
                    trick.setType(TrickType.SCRIPT);
                    // Update for CodeBlocks ->
                    var script = args.getFrom(args.getArgIndex("-script") + 1);
                    if (script == null || script.isEmpty()) {
                        dMessage.apply(message.reply("Script Cannot be null/empty!")).queue();
                        return CommandResult.PASS;
                    }
                    trick.setScript(script);
                } else if (args.hasArg("-alias")) {
                    // Check if Target is Normal/Scriptable too! Can be added later...

                    trick.setType(TrickType.ALIAS);
                    // Update for CodeBlocks ->
                    var alias = args.getFrom(args.getArgIndex("-alias") + 1);
                    if (alias == null || alias.isEmpty()) {
                        dMessage.apply(message.reply("Trick Cannot be null/empty!")).queue();
                        return CommandResult.PASS;
                    }

                    trick.setAliasTarget(alias);
                }

                trick.setLastUserEdited(member.getIdLong());
                trick.setLastEdited(System.currentTimeMillis());

                save(trick);

                dMessage.apply(message.reply("Modified Trick %s!".formatted(trickID))).queue();
                return CommandResult.PASS;
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(trickID))).queue();
            }
        } else if (type == TrickCMDType.REMOVE) {
            if (exists(trickID, guildID)) {
                var trick = getTrick(trickID, guildID);

                if (!isOwnerAndUnlocked(trick, member)) {
                    dMessage.apply(message.reply("Cannot modify/remove Trick '%s' as your not the owner of this trick and its locked.".formatted(trickID))).queue();
                    return CommandResult.PASS;
                }

                delete(trick);
                TRICKS.remove(new TrickKey(trickID, guildID));
                dMessage.apply(message.reply("Removed Trick %s.".formatted(trickID))).queue();
            }
        } else if (type == TrickCMDType.INFO) {
            if (!exists(trickID, guildID)) {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(trickID))).queue();
                return CommandResult.PASS;
            }

            var trick = getTrick(trickID, guildID);

            String details = """
                    Details for Trick %s
                    
                    Type -> %s
                    Owner -> <@%s>
                    Created -> <t:%s:d> <t:%s:T>
                    LastUserEdited -> <@%s>
                    LastEdited -> <t:%s:d> <t:%s:T>
                    isLocked -> %s
                    isSuppressed -> %s
                    Times Used -> %s
                  
                    Extra Info: https://mangobot.mangorage.org/trick?guildId=%s&trickId=%s
                    """
                    .formatted(
                            trick.getTrickID(),
                            trick.getType(),
                            trick.getOwnerID(),
                            trick.getCreated() / 1000, // Discord expects it to be in Epoch Seconds, not ms
                            trick.getCreated() / 1000, // Discord expects it to be in Epoch Seconds, not ms
                            trick.getLastUserEdited(),
                            trick.getLastEdited() / 1000, // Discord expects it to be in Epoch Seconds, not ms
                            trick.getLastEdited() / 1000, // Discord expects it to be in Epoch Seconds, not ms
                            trick.isLocked(),
                            trick.isSuppressed(),
                            trick.getTimesUsed(),
                            trick.getGuildID(),
                            trick.getTrickID()
                    );

            dMessage.apply(message.reply(details))
                    .setSuppressedNotifications(true)
                    .setSuppressEmbeds(true)
                    .setAllowedMentions(List.of())
                    .queue();

        } else if (type == TrickCMDType.SHOW) {
            if (!exists(trickID, guildID)) {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(trickID))).queue();
                return CommandResult.PASS;
            }

            var trick = getTrick(trickID, guildID);
            useTrick(trick, message, message.getChannel(), guildID, args);
        } else if (type == TrickCMDType.LIST) {
            int length;

            if (trickID != null) {
                try {
                    length = Integer.parseInt(trickID);
                } catch (NumberFormatException e) {
                    length = 5;
                }
            } else {
                length = 5;
            }

            MessageChannelUnion channel = message.getChannel();
            if (!getTricksForGuild(guildID).isEmpty()) {

                PagedList<String> tricks = createTricks(guildID, length);

                channel.sendMessage("""
                        Getting Tricks List... 
                        """).queue((m -> {
                            PAGES.put(m.getId(), tricks);
                            TaskScheduler.getExecutor().schedule(new RunnableTask<>(m, (d) -> removeTricksList(d.get())), 10, TimeUnit.MINUTES);
                            updateTrickListMessage(tricks, m, true);
                        })
                );
            }
            return CommandResult.PASS;
        } else if (type == TrickCMDType.LOCK) {
            if (!exists(trickID, guildID)) {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(trickID)));
                return CommandResult.PASS;
            }

            var trick = getTrick(trickID, guildID);
            if (trick.getOwnerID() == member.getIdLong()) {
                dMessage.apply(
                        message.reply((trick.isLocked() ? "Unlocked" : "Locked") + " Trick '%s'".formatted(trickID))
                ).queue();
                trick.setLock(!trick.isLocked());
                save(trick);
            } else {
                dMessage.apply(message.reply("Can only lock/unlock your own Tricks!")).queue();
            }
        } else if (type == TrickCMDType.TRANSFER) {
            // TODO: Add transfer ability...
        } else if (type == TrickCMDType.FIND) {
            var tricks = getTricksForGuild(guildID);
            if (trickID == null) trickID = ".*"; // Match any on error
            String finalTrickID = trickID;
            tricks = tricks.stream().filter((trick) ->
                    trick.getTrickID().contains(finalTrickID)
                            || trick.getTrickID().matches(finalTrickID)).collect(Collectors.toList());
            MessageChannelUnion channel = message.getChannel();
            if (!tricks.isEmpty()) {
                PagedList<String> trickList = createTricks(guildID, tricks);
                channel.sendMessage("""
                    Finding matching tricks... 
                    """).queue((m -> {
                            PAGES.put(m.getId(), trickList);
                            TaskScheduler.getExecutor().schedule(new RunnableTask<>(m, (d) -> removeTricksList(d.get())), 10, TimeUnit.MINUTES);
                            updateTrickListMessage(trickList, m, true);
                        })
                );


            } else {
                channel.sendMessage("""
                            No matching tricks found
                            """).queue();
            }
            return CommandResult.PASS;
        }

        /*
        !trick -s trickID
        !trick -r trickID
        !trick -i trickID
        !trick -l <10>
        !trick -f <trickname or regex>

        !trick -e trickID -suppress -content Hello There!
        !trick -e trickID -script msg.reply(''Hello!');
        !trick -e trickID -alias targetID
`
        !trick -a trickID -suppress -content Hello There!
        !trick -a trickID -alias targetID
        !trick -a trickID -script msg.reply(''Hello!');
         */


        return CommandResult.PASS;
    }

    private void useTrick(Trick trick, Message message, MessageChannel channel, long guildID, Arguments args) {
        MessageSettings dMessage = plugin.getMessageSettings();
        var type = trick.getType();
        var replyTarget = message.getMessageReference() == null ? null : message.getMessageReference().getMessage();
        boolean shouldPing = false;
        if (replyTarget != null && replyTarget.getMember() != null) {
            shouldPing = replyTarget.getMember().getRoles()
                    .stream().noneMatch((role) -> role.isHoisted() ^ role.getName().equals("Patreons"));
        }
        if (type == TrickType.NORMAL) {
            dMessage.withButton(
                    dMessage.apply(channel.sendMessage(trick.getContent()))
                            .setSuppressEmbeds(trick.isSuppressed()), MangoBot.ACTION_REGISTRY.get(TrashButtonAction.class).createForUser(message.getAuthor())
            ).setAllowedMentions(
                    Arrays.stream(Message.MentionType.values())
                            .filter(t -> {
                                if (t == Message.MentionType.EVERYONE) return false;
                                if (t == Message.MentionType.HERE) return  false;
                                return true;
                            })
                            .toList()
            ).setMessageReference(replyTarget).mentionRepliedUser(shouldPing).queue();
            trick.use();
            save(trick);
        } else if (type == TrickType.ALIAS) {
            if (exists(trick.getAliasTarget(), guildID)) {
                var alias = getTrick(trick.getAliasTarget(), guildID);
                trick.use();
                useTrick(alias, message, channel, guildID, args);
            }
            // Cannot use Scripts with slash commands
        } else if (type == TrickType.SCRIPT && message != null) {
            if (!ALLOW_SCRIPT_TRICKS) {
                dMessage.apply(message.reply("Scriptable Tricks currently disabled...")).queue();
            } else {
                // Execute code
                trick.use();
                var script = MarkdownSanitizer.sanitize(trick.getScript());
                SCRIPT_RUNNER.execute(
                        trick,
                        script,
                        message,
                        channel,
                        args.getArgs().length > 0 ? args.getFrom(2).split(" ") : new String[]{}
                );
            }
        }
    }

    @Override
    public List<String> commands() {
        return List.of("trick");
    }

    @Override
    public String usage() {
        return """
                ## `!tricks`
                `-a` to add, `-e` to edit, `-s` to view source, `-r` to remove, `-l` to list.
                - To run a trick, use its ID as if it were another command. E.g.: `!drivers`
                - When adding or editing, you can optionally add the `-supress` arg to supress embeds in your trick's links.
                - When listing tricks, you can optionally specify how many you want per page.
               
               
                ## Examples:
                
                ## How to add tricks: 
                `!tricks -a exampletrick -content this is an example trick`
                `!tricks -a exampletrickalias -alias exampletrick`
                
                ## How to modify tricks:
                `!tricks -e exampletrick -suppress -content editing the trick. https://bing.com`
                `!tricks -e exampletrickalias -alias exampletrick`
                
                ## How to lock/unlock a trick:
                `!tricks -lock exampletrick` // Do it again to unlock
               
                ## How to show tricks:
                `!tricks -s exampletrick`
                
                ## How to remove tricks:
                `!tricks -r exampletrick`
                
                ## How to show a list of tricks:
                `!tricks -l 10`
                
                ## How to find tricks:
                `!tricks -f installerlogs`
                `!tricks -f create.*`
                """;
    }

    private void removeTricksList(Message message) {
        if (PAGES.containsKey(message.getId())) {
            message.editMessage(createTricksString(PAGES.get(message.getId()))).setComponents().queue();
            PAGES.remove(message.getId());
        }
    }

    private void updateTrickListMessage(PagedList<String> tricks, Message message, boolean addButtons, String buttonID) {
        switch (buttonID) {
            case "next" -> tricks.next();
            case "prev" -> tricks.previous();
        }

        String result = createTricksString(tricks);

        if (addButtons) {
            // Add buttons!
            Button prev = Button.primary("prev".formatted(message.getId()), "previous");
            Button next = Button.primary("next".formatted(message.getId()), "next");

            message.editMessage(result).setActionRow(prev, next).queue();
        } else {
            message.editMessage(result).queue();
        }
    }

    private void updateTrickListMessage(PagedList<String> tricks, Message message, boolean addButtons) {
        updateTrickListMessage(tricks, message, addButtons, "");
    }

    private String createTricksString(PagedList<String> tricks) {
        String result = "List of Tricks (%s / %s) \r".formatted(tricks.getPage(), tricks.totalPages());

        PagedList.Page<String> entries = tricks.current();

        int i = 0;
        for (String entry : entries.getEntries()) {
            i++;
            result = result + "%s: %s \r".formatted(i, entry);
        }

        return result;
    }

    private PagedList<String> createTricks(long guildID, int entries) {
        PagedList<String> tricks = new PagedList<>();

        Object[] LIST = getTricksForGuild(guildID).stream().map(Trick::getTrickID).toArray();
        tricks.rebuild(Arrays.copyOf(LIST, LIST.length, String[].class), entries);

        return tricks;
    }

    private PagedList<String> createTricks(long guildID, List<Trick> tricks) {
        PagedList<String> trickList = new PagedList<>();

        Object[] LIST = tricks.stream().map(Trick::getTrickID).toArray();
        trickList.rebuild(Arrays.copyOf(LIST, LIST.length, String[].class), tricks.size());

        return trickList;
    }

    public void onButton(DiscordButtonInteractEvent event) {
        var interaction = event.getDiscordEvent();

        Message message = interaction.getMessage();
        String ID = message.getId();

        if (PAGES.containsKey(ID)) {
            updateTrickListMessage(PAGES.get(ID), message, false, interaction.getButton().getId());
            interaction.getInteraction().deferEdit().queue();
        }
    }
}

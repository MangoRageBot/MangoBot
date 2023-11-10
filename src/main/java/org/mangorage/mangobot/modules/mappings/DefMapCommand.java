package org.mangorage.mangobot.modules.mappings;

import org.mangorage.mangobot.Core;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.util.MessageSettings;

import net.dv8tion.jda.api.entities.Message;

public class DefMapCommand implements IBasicCommand {

	
	public MappingsManager manager;
	public Core core;

	public DefMapCommand(MappingsManager mappings_manager,Core core) {
		// TODO Auto-generated constructor stub
	this.manager=mappings_manager;
	this.core = core;
	}

	
	@Override
	public CommandResult execute(Message event, Arguments args) {
		// TODO Auto-generated method stub
		MessageSettings dMessage = core.getMessageSettings();
		dMessage.apply(event.reply(manager.defmap(String.join(" ",args.getArgs())))).queue();		
		return CommandResult.PASS;
	}

	@Override
	public String commandId() {
		// TODO Auto-generated method stub
		return "defmap";
	}

}

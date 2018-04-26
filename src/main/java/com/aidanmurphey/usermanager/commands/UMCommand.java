package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import org.bukkit.command.CommandSender;
import java.util.List;

public interface UMCommand {

    List<String> getAliases();

    void execute(CommandSender sender, String[] args) throws CommandFailedException;

}

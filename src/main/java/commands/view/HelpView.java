package main.java.commands.view;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class HelpView {
    private static final Color DARK_BLUE = new Color(13, 58, 143);
    public static MessageCreateData getHelpView(){

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Logos Bot")
                .setDescription("Open source discord bot with music features and crypto commands! Play your favorite songs directly from YouTube.")
                .setThumbnail("https://cdn-icons-png.flaticon.com/512/749/749024.png?w=826&t=st=1689123378~exp=1689123978~hmac=e975ec34409fd04e619d0f301ab29850bfdebdc13c749d2ee39b9f00bd8dcf9c")
                .setColor(DARK_BLUE)
                .addField("GitHub Repository", "https://github.com/yagodaoud/LogosBot", false)
                .addField("Send your Feedback!", "yagodaouddev@gmail.com or `krdzz` on discord ", false)
                .addField("Help", "Get help below", false)
                .build();

        ActionRow actionRow = ActionRow.of(
                StringSelectMenu.create("menu:help")
                        .setPlaceholder("Select the command type you need help with:")
                        .addOption("Music", "Music")
                        .addOption("Crypto", "Crypto")
                        .setRequiredRange(1, 1)
                        .build().asEnabled());

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                .addEmbeds(builder.build())
                .addComponents(actionRow);

        return messageBuilder.build();
    }

}

package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@ConditionalOnBean(TelegramBotsApi.class)
public class MyWishBot extends TelegramLongPollingBot {
    @Autowired
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    private ChatPersister chatFilePersister;

    @Autowired(required = false)
    private InformationProvider informationProvider;

    @Autowired
    private List<BotCommand> commands;

    @Getter
    @Value("${io.mywish.bot.token}")
    private String botToken;
    @Getter
    @Value("${io.mywish.bot.name}")
    private String botUsername;

    public MyWishBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @PostConstruct
    protected void init() {
        try {
            telegramBotsApi.registerBot(this);
            log.info("Bot was registered, token: {}.", botToken);
        }
        catch (TelegramApiRequestException e) {
            log.error("Failed during the bot registration.", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;

        if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
        }
        else if (update.hasEditedChannelPost()) {
            chatId = update.getEditedChannelPost().getChatId();
        }
        else if (update.hasEditedMessage()) {
            chatId = update.getEditedMessage().getChatId();
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom() != null
                    ? update.getMessage().getFrom().getUserName()
                    : null;
            ChatContext chatContext = new ChatContext(this, chatId, userName);
            List<String> args = new ArrayList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            String cmdName = args.remove(0);
            Optional<BotCommand> botCommand = commands
                    .stream()
                    .filter(cmd ->
                            cmd.getName().equals(cmdName)
                    )
                    .findFirst();
            if (botCommand.isPresent()) {
                botCommand.get().execute(chatContext, args);
            }
            else {
                log.warn("Unknown command {} from user {} in chat {}", cmdName, userName, chatId);
                chatContext.sendMessage("Unknown command '" + cmdName + "'");
            }
        }
        else {
            return;
        }
        if (chatFilePersister.tryAdd(chatId, botUsername)) {
            log.info("Bot '{}' was added to the chat {}. Now he is in {} chats.", botUsername, chatId, chatFilePersister.getCount());
        }
    }

    public void onContract(String network, Integer productId, String productType, Integer id, String cost, String user, String address, String addressLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": new ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(") was created for ")
                .append(cost)
                .append(" by ")
                .append(user)
                .append(", see on <a href=\"")
                .append(addressLink)
                .append("\">")
                .append(address)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapsOrder(String network, Integer productId, String name, String transactionHash, String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": new SWAPS2 Order (")
                .append(productId)
                .append(") was created, see on [")
                .append(transactionHash)
                .append("](")
                .append(txLink)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onSwaps2Deposit(String network, Integer productId, String transactionHash, String txLink,
                                String symbol, String userIdOrEmail) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS2 (")
                .append(productId)
                .append(") deposit of ")
                .append(symbol)
                .append(" was made by user ")
                .append(userIdOrEmail)
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwaps2Completed(String network, String transactionHash, String txLink, Integer productId) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS2 (")
                .append(productId)
                .append(") was completed")
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwaps2Canceled(String network, String transactionHash, String txLink, Integer productId) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS2 (")
                .append(productId)
                .append(") was canceled")
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwaps2Refund(String network, Integer productId, String transactionHash, String txLink,
                               String symbol, String userIdOrEmail) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS2 (")
                .append(productId)
                .append(") refund of ")
                .append(symbol)
                .append(" was made by user ")
                .append(userIdOrEmail)
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapCompleted(String network, String transactionHash, String txLink,
                                Integer id, Integer productId) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(") was completed")
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapCanceled(String network, String transactionHash, String txLink,
                               Integer id, Integer productId) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(") was canceled")
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapsRefund(String network, String transactionHash, String txLink,
                              String userAddress) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS refund ")
                .append("was send to ")
                .append(userAddress)
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapsDeposit(String network, String transactionHash, String txLink,
                               String userAddress) {
        final String message = new StringBuilder()
                .append(network)
                .append(": SWAPS deposit ")
                .append("was send from ")
                .append(userAddress)
                .append(". See on ")
                .append("<a href=\"")
                .append(txLink)
                .append("\">")
                .append(transactionHash)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onSwapsOrderFromDataBase(Integer productId, String name, String user) {
        final String message = new StringBuilder()
                .append("DB: new SWAPS Order (")
                .append(productId)
                .append(") (")
                .append(name)
                .append(") was created by ")
                .append(user)
                .toString();

        sendToAll(message);
    }

    public void onContractFailed(String network, Integer productId, String productType, Integer id, final String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": *failed* [contract creation ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(")!](")
                .append(txLink)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onBalance(String network, String account, String cost, final String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": payment received from user ")
                .append(account)
                .append(", <a href=\"https://")
                .append(txLink)
                .append("\">")
                .append(cost)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void onFGWBalanceChanged(String network, String delta, String balance, String link, long blockNo, String blockLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": federation GW balance changed on ")
                .append(delta)
                .append(", now it is [")
                .append(balance)
                .append("](")
                .append(link)
                .append(") at block [")
                .append(blockNo)
                .append("](")
                .append(blockLink)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onBtcPayment(String network, String productType, long productId, String value, final String link) {
        final String message = new StringBuilder()
                .append(network)
                .append(": funds arrived for contract ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append("): [")
                .append(value)
                .append("](")
                .append(link)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onNeoPayment(final String network, final String address, final String value, final String link) {
        final String message = new StringBuilder()
                .append(network)
                .append(": funds arrived on address ")
                .append(address)
                .append(": [")
                .append(value)
                .append("](")
                .append(link)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void sendToAll(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview();
        sendToAllChats(sendMessage);
    }

    public void sendToAllWithMarkdown(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview()
                .enableMarkdown(true);
        sendToAllChats(sendMessage);
    }

    public void sendToAllWithHtml(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview()
                .enableHtml(true);
        sendToAllChats(sendMessage);
    }

    private void sendToAllChats(SendMessage sendMessage) {
        for (long chatId : chatFilePersister.getChatsByBotName(botUsername)) {
            try {
                // it's ok to specify chat id, because sendMessage will be serialized to JSON during the call
                execute(sendMessage.setChatId(chatId));
            } catch (TelegramApiException e) {
                log.error("Sending message '{}' to chat '{}' was failed.", sendMessage.getText(), chatId, e);
                chatFilePersister.remove(chatId);
            }
        }

    }

    private void directMessage(long chatId, String userName) {
        if (informationProvider == null || !informationProvider.isAvailable(userName)) {
            try {
                execute(new SendMessage()
                        .setChatId(chatId)
                        .setText("Not information available.")
                );
            }
            catch (TelegramApiException e) {
                log.error("Sending stub message to chat '{}' was failed.", chatId, e);
            }
            return;
        }
        SendMessage message = informationProvider.getInformation(userName);

        try {
            execute(message.setChatId(chatId));
        }
        catch (TelegramApiException e) {
            log.error("Sending message '{}' to chat '{}' was failed.", message, chatId, e);
        }
    }
}

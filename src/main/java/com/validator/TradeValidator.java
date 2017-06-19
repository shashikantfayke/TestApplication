package com.validator;


import com.domain.Notification;
import com.domain.Trade;
import com.service.TradeStoreService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TradeValidator {

    private static final String LOWER_VERSION_RECEIVED = "Lower Version Received";
    private static final String SAME_VERSION_FOUND = "Same Version Found";
    private static final String MATURITY_DATE_IS_LESS_THEN_TODAYS_DATE = "Maturity Date Is Less Then Todays Date";
    private static final String TRADE_IS_ALREADY_MATURED = "Trade is Already Matured";
    TradeStoreService tradeStoreService;
    Notification notification;

    public TradeValidator(TradeStoreService tradeStoreService) {
        this.tradeStoreService = tradeStoreService;
    }

    public Notification validate(Trade newTrade) {
        notification = new Notification();
        List<String> messages = new ArrayList<>();
        List<Trade> allTrades = tradeStoreService.getAllTrades();
        final Trade oldTrade = getOldStoredTrade(newTrade, allTrades);

        if (newTrade.getVersion().intValue() < oldTrade.getVersion().intValue()) {

            throw new RuntimeException(LOWER_VERSION_RECEIVED);
        }

        if (newTrade.getVersion().intValue() == oldTrade.getVersion().intValue()) {
            overrideNewlyTrade(newTrade, allTrades, oldTrade);
            messages.add(SAME_VERSION_FOUND);

        }

        if (newTrade.getMaturityDate().isBefore(LocalDate.now())) {

            messages.add(MATURITY_DATE_IS_LESS_THEN_TODAYS_DATE);
        }
        if (newTrade.getMaturityDate().isAfter(LocalDate.now())) {
            updateExpiredFlag(newTrade, allTrades, oldTrade);
            messages.add(TRADE_IS_ALREADY_MATURED);
        }
        notification.setMessages(messages);
        return notification;

    }

    private void overrideNewlyTrade(Trade newTrade, List<Trade> allTrades, Trade oldTrade) {
        allTrades.remove(oldTrade);
        allTrades.add(newTrade);
    }

    private void updateExpiredFlag(Trade newTrade, List<Trade> allTrades, Trade oldTrade) {
        newTrade.setIsExpired("Y");
        overrideNewlyTrade(newTrade, allTrades, oldTrade);
    }

    private Trade getOldStoredTrade(Trade t1, List<Trade> allTrades) {
        return allTrades.stream()
                .filter(trade -> trade.getTradeId().equals(t1.getTradeId()))
                .findAny().get();
    }
}

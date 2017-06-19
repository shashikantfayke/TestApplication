package com.validator;

import com.domain.Notification;
import com.domain.Trade;
import com.service.TradeStoreService;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TradeValidatorTest {

    private static final String SAME_VERSION_FOUND = "Same Version Found";
    private static final String MATURITY_DATE_IS_LESS_THEN_TODAYS_DATE = "Maturity Date Is Less Then Todays Date";
    private static final String TRADE_IS_ALREADY_MATURED = "Trade is Already Matured";
    private TradeValidator tradeValidator;

    @Mock
    TradeStoreService tradeStoreService;

    @Before
    public void setUp() throws Exception {
        tradeValidator = new TradeValidator(tradeStoreService);

    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfLowerVersionReceived() throws Exception {
        Trade t1 = new Trade("T1", 0, "CP-1", "B1", LocalDate.of(2020, 05, 20), LocalDate.now(), "N");

        when(tradeStoreService.getAllTrades()).thenReturn(getTrades());

        tradeValidator.validate(t1);


    }

    @Test
    public void shouldOverrideIfVersionIsSame() throws Exception {
        Trade t1 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2020, 05, 20), LocalDate.now(), "N");

        when(tradeStoreService.getAllTrades()).thenReturn(getTrades());

        Notification notification = tradeValidator.validate(t1);

        assertThat(notification.getMessages().get(0), Is.is(SAME_VERSION_FOUND));


    }

    @Test
    public void shouldNotAllowIfMaturityDateIsLessThenTodaysDate() throws Exception {
        Trade t1 = new Trade("T1", 4, "CP-1", "B1", LocalDate.of(2011, 05, 20), LocalDate.now(), "N");

        when(tradeStoreService.getAllTrades()).thenReturn(getTrades());

        Notification notification = tradeValidator.validate(t1);

        assertThat(notification.getMessages().get(0), Is.is(MATURITY_DATE_IS_LESS_THEN_TODAYS_DATE));

    }

    @Test
    public void shouldUpdateExpireFlagIfMaturityDateCrosses() throws Exception {
        Trade t1 = new Trade("T1", 4, "CP-1", "B1", LocalDate.of(2020, 05, 20), LocalDate.now(), "N");

        when(tradeStoreService.getAllTrades()).thenReturn(getTrades());

        Notification notification = tradeValidator.validate(t1);

        assertThat(notification.getMessages().get(0), Is.is(TRADE_IS_ALREADY_MATURED));

    }

    private List<Trade> getTrades() {
        Trade t1 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2020, 05, 20), LocalDate.now(), "N");
        Trade t2 = new Trade("T2", 2, "CP-2", "B1", LocalDate.of(2021, 05, 20), LocalDate.now(), "N");
        Trade t3 = new Trade("T2", 1, "CP-1", "B1", LocalDate.of(2021, 05, 20), LocalDate.now(), "N");
        Trade t4 = new Trade("T3", 3, "CP-3", "B1", LocalDate.of(2020, 05, 20), LocalDate.now(), "Y");

        List<Trade> trades = new ArrayList<>();
        trades.add(t1);
        trades.add(t2);
        trades.add(t3);
        trades.add(t4);
        return trades;

    }


}
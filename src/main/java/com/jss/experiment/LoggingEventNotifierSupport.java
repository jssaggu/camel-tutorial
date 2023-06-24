package com.jss.experiment;

import org.apache.camel.Exchange;
import org.apache.camel.impl.event.ExchangeCreatedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * This class can be used to capture Camel events How to use: Put the below code where you like to
 * use this support
 *
 * <p>getContext().getManagementStrategy().addEventNotifier(new LoggingEventNotifierSupport());
 */
public class LoggingEventNotifierSupport extends EventNotifierSupport {
    static int index = 0;

    @Override
    public void notify(CamelEvent event) throws Exception {

        System.out.println(
                "JSS [" + event.getType() + "] [" + event + "] [" + event.getSource() + "] ");

        if (event instanceof ExchangeCreatedEvent) {
            index++;
            Exchange exchange = ((ExchangeCreatedEvent) event).getExchange();
            String exIndex = exchange.getProperty("JSS", String.class);
            if (StringUtils.isEmpty(exIndex)) {
                MDC.put("JSS", (index) + "");
                exchange.setProperty("JSS", index);
            }

            System.out.println(
                    "JSS [ExchangeCreatedEvent] "
                            + "["
                            + event
                            + "] "
                            + "["
                            + event.getSource()
                            + "] "
                            + "[MDC: "
                            + MDC.get("JSS")
                            + "]"
                            + "[Exc:"
                            + exchange.getProperty("JSS", String.class)
                            + "]");
        } else if (event instanceof CamelEvent.ExchangeCompletedEvent) {
            System.out.println(
                    "JSS [ExchangeCompletedEvent] ["
                            + event
                            + "] ["
                            + event.getSource()
                            + "] [MDC: "
                            + MDC.get("JSS")
                            + "]");
        }
    }
}

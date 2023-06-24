package com.jss.experiment.cl10n;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import java.util.Locale;

public class ColourMain {
    public static void main(String[] args) {
        IMessageConveyor mc = new MessageConveyor(Locale.ENGLISH);

        // use it to retrieve internationalized messages
        String red = mc.getMessage(Colors.RED);
        String blue = mc.getMessage(Colors.BLUE);
        String green = mc.getMessage(Colors.GREEN, "hara");
        //        String json = mc.getMessage(LoggingLabels.IPS_TP_0001_PARSE_TEMPLATED_JSON_ERROR);

        //        System.out.printf(">> " + json);
    }
}

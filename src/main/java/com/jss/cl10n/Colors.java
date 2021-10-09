package com.jss.cl10n;

import ch.qos.cal10n.LocaleData;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.BaseName;

@BaseName("colors")
@LocaleData( { @Locale("en"), @Locale("fr") })
public enum Colors  {
    BLUE("Neela", "Dho Dalo"),
    RED("Laal", "Obal Dalo"),
    GREEN("Hara", "Jameen per hai");

    private final String reason;
    private final String resolution;

    Colors(String reason, String resolution) {
        this.reason = reason;
        this.resolution = resolution;
    }
}
package com.jss.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionDto implements Serializable {

    private String transactionId;
    private String senderAccountId;
    private String receiverAccountId;
    private String amount;
    private String currency;
    private String transactionDate;
}

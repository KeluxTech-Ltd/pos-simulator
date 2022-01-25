package com.jayrush.springmvcrest.Notification;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author JoshuaO
 */

@Data
public class pushWithdrawalPTSPRequest{

    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("terminalId")
    @Expose
    private String terminalId;
    @SerializedName("statusCode")
    @Expose
    private String statusCode;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("rrn")
    @Expose
    private String rrn;
    @SerializedName("reversal")
    @Expose
    private Boolean reversal;
    @SerializedName("stan")
    @Expose
    private String stan;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("transactionType")
    @Expose
    private String transactionType;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("transactionTime")
    @Expose
    private String transactionTime;


}

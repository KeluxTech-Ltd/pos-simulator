package com.jayrush.springmvcrest.Notification;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.bankServiceRepo;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.bank;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import okhttp3.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author JoshuaO
 */

@Component
public class MedusaNotification {
    @Autowired
    TerminalRepository terminalRepository;
    @Autowired
    bankServiceRepo bankServiceRepo;
    @Autowired
    InstitutionRepository institutionRepository;

    private static final Logger logger = LoggerFactory.getLogger(MedusaNotification.class);

    public String  NotifyInstitution(TerminalTransactions transactionRecords) throws IOException, BadPaddingException, IllegalBlockSizeException, JSONException, DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Gson gson = new Gson() ;
        String respBody;
        Response res = null;
        int count = 0;
        pushWithdrawalPTSPRequest pushWithdrawalPTSPRequest = new pushWithdrawalPTSPRequest();
        Institution institution = institutionRepository.findByInstitutionID(transactionRecords.getInstitutionID());

        String url = institution.getInstitutionURL() + institution.getInstitutionIntegrationVersion();
        logger.info("Notification URL is {}",url);
        String terminalID = transactionRecords.getTerminalID();
        String bankcode = terminalID.substring(0,4);
        bank bank = bankServiceRepo.findByCbnCode(bankcode);
        if (Objects.isNull(bank)){
            logger.info("No bank found for {}",bankcode);
            pushWithdrawalPTSPRequest.setBank("No bank found");
        }else {
            pushWithdrawalPTSPRequest.setBank(bank.getBankName());
        }

        MediaType MEDIA_TYPE = MediaType.parse("application/json");



        int decimalPlaces = 2;
        BigDecimal bigDecimalCurrency=new BigDecimal(transactionRecords.getAmount());
        bigDecimalCurrency = bigDecimalCurrency.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
        bigDecimalCurrency = bigDecimalCurrency.multiply(new BigDecimal(100));

        String amount = bigDecimalCurrency.toString();

        pushWithdrawalPTSPRequest.setAmount(amount);
        pushWithdrawalPTSPRequest.setTerminalId(transactionRecords.getTerminalID());
        pushWithdrawalPTSPRequest.setStatusCode(transactionRecords.getResponseCode());
        pushWithdrawalPTSPRequest.setPan(transactionRecords.getPan());
        pushWithdrawalPTSPRequest.setRrn(transactionRecords.getRrn());
        if (transactionRecords.getStatus().equalsIgnoreCase("reversal"))
            {
                pushWithdrawalPTSPRequest.setReversal(true);
            }
        else
            {
                pushWithdrawalPTSPRequest.setReversal(false);
            }
        pushWithdrawalPTSPRequest.setStan(transactionRecords.getStan());
        pushWithdrawalPTSPRequest.setTransactionType("3Line");
        pushWithdrawalPTSPRequest.setProductId(transactionRecords.getProcessedBy());
        pushWithdrawalPTSPRequest.setTransactionTime(transactionRecords.getRequestDateTime());

        JSONObject jsonObject = new JSONObject();
        String bodyAsString = gson.toJson(pushWithdrawalPTSPRequest);
        logger.info("JSON TO STRING {}", bodyAsString);
//        logger.info("Notification AppKey {}",institution.getInstitutionAppKey());
        byte [] key = Hex.decodeHex(institution.getInstitutionAppKey().toCharArray());
        Cipher cipher = Cipher.getInstance("AES");

        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, originalKey);
        byte[] stringBytes = bodyAsString.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedByte = cipher.doFinal(stringBytes);
//        System.out.println(Arrays.toString(encryptedByte));
        String encryptedString = Base64.encodeBase64String(encryptedByte);
        logger.info("encrypted is {}", encryptedString);
        jsonObject.put("request", encryptedString);
        logger.info("Json object to string is {}",jsonObject.toString());

        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                //  .addHeader("Content-Type", "application/json")
                    //  .addHeader("Authorization", "Bearer " + DeviceDetails.oauthToken)
                .post(body)
                .build();

        //Create a new call object with POST method
        OkHttpClient client = new OkHttpClient();
        client = new OkHttpClient.Builder()

                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS).build();

        client.readTimeoutMillis();
        Call call = client.newCall(request);
        okhttp3.Response response = null;
        response = call.execute();


        //Check if Call is successful
        if (response.isSuccessful()) {
            //Get Okhttp Response Code
            int respCode = response.code();
            logger.info("Code: {}" ,respCode);

            //Get OkHttp Message
            String respMessage = response.message();
            logger.info("Message: {}" ,respMessage);

            //Get OkHttp Resp Body
            respBody = response.body().string();
            logger.info("Response Body: {} ",respBody);

            res = gson.fromJson(respBody, Response.class);
        }
        else{
            //Get OkHttp Resp Body
            respBody = response.body().string();
            logger.info("Response Body: {}"  , respBody);

            //Get Okhttp Response Code
            int respCode = response.code();
            logger.info("Code: {}" , respCode);

            //Get OkHttp Message
            String respMessage = response.message();
            logger.info("Message: {}", respMessage);

            res = gson.fromJson(respBody, Response.class);
        }
        count++;
        return respBody;
    }

    public static void main(String[]args) throws DecoderException {
        String appkey = "b697772320da7258386eb3b002833d13";
        byte[]key = Hex.decodeHex(appkey.toCharArray());
        System.out.println(key);
    }

}


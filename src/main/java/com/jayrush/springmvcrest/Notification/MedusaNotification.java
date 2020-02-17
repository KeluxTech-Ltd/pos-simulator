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
//    private static final String INTERGRATION_VERSION_LIVE = "/integration/8MNMZCE1BG/api/v1/pos/transaction/log/";
//    private static final String APPKEY_LIVE = "77ecdc14a4b4f2f78323a996d9e5a0f6";
//    public static String BASE_URL2 = "freedom.3lineng.com:8080";

    private static final String INTERGRATION_VERSION_TEST = "/integration/A9NU2AYCA6/api/v1/pos/transaction/log/";
    private static final String APPKEY_TEST = "b697772320da7258386eb3b002833d13";
    public static String BASE_URL2 = "10.2.2.47:7070";

    public static int DEFAULT = 0;
    private static final String API_VERSION = "/gravity/api";
    private static final Logger logger = LoggerFactory.getLogger(MedusaNotification.class);

//    public static String  SyncTrans(TerminalTransactions transactionRecords) throws IOException, BadPaddingException, IllegalBlockSizeException, JSONException, DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        Gson gson = new Gson() ;
//        String respBody;
//        Response res = null;
//        int count = 0;
//        String url = "http://" + BASE_URL2 + INTERGRATION_VERSION_TEST;
//
//        MediaType MEDIA_TYPE = MediaType.parse("application/json");
//
//        pushWithdrawalPTSPRequest pushWithdrawalPTSPRequest = new pushWithdrawalPTSPRequest();
//
//        int decimalPlaces = 2;
//        BigDecimal bigDecimalCurrency=new BigDecimal(transactionRecords.getAmount());
//        bigDecimalCurrency = bigDecimalCurrency.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
//        bigDecimalCurrency = bigDecimalCurrency.multiply(new BigDecimal(100));
//
//        String amount = bigDecimalCurrency.toString();
//
//        pushWithdrawalPTSPRequest.setAmount(amount);
//        pushWithdrawalPTSPRequest.setTerminalId(transactionRecords.getTerminalID());
//        pushWithdrawalPTSPRequest.setStatusCode(transactionRecords.getResponseCode());
//        pushWithdrawalPTSPRequest.setPan(transactionRecords.getPan());
//        pushWithdrawalPTSPRequest.setRrn(transactionRecords.getRrn());
//        if (transactionRecords.getStatus().equalsIgnoreCase("reversal"))
//            {
//                pushWithdrawalPTSPRequest.setReversal(true);
//            }
//        else
//            {
//                pushWithdrawalPTSPRequest.setReversal(false);
//            }
//        pushWithdrawalPTSPRequest.setStan(transactionRecords.getStan());
//        String TID = transactionRecords.getTerminalID();
//        String bankcode = TID.substring(0,4);
//        logger.info("Bank Code: {}", bankcode);
//        if(bankcode.equalsIgnoreCase("2101")){
//            pushWithdrawalPTSPRequest.setBank("Providus Bank");
//        }else if(bankcode.equalsIgnoreCase("2070")){
//            pushWithdrawalPTSPRequest.setBank("Fidelity Bank");
//        }
//
//        pushWithdrawalPTSPRequest.setTransactionType("3Line");
//        pushWithdrawalPTSPRequest.setProductId("3LINE001");
//        pushWithdrawalPTSPRequest.setTransactionTime(transactionRecords.getRequestDateTime());
//        JSONObject jsonObject = new JSONObject();
//        String bodyAsString = gson.toJson(pushWithdrawalPTSPRequest);
//        logger.info("JSON TO STRING {}", bodyAsString);
//        byte [] key = Hex.decodeHex(APPKEY_TEST.toCharArray());
//        Cipher cipher = Cipher.getInstance("AES");
//
//        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, originalKey);
//        byte[] stringBytes = bodyAsString.getBytes(StandardCharsets.UTF_8);
//        byte[] encryptedByte = cipher.doFinal(stringBytes);
//        String encryptedString = Base64.encodeBase64String(encryptedByte);
//        logger.info("encrypted is {}", encryptedString);
//        jsonObject.put("request", encryptedString);
//
//        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
//        Request request = new Request.Builder()
//                .url(url)
//                //  .addHeader("Content-Type", "application/json")
//                    //  .addHeader("Authorization", "Bearer " + DeviceDetails.oauthToken)
//                .post(body)
//                .build();
//
//            //Create a new call object with POST method
//        OkHttpClient client = new OkHttpClient();
//        Call call = client.newCall(request);
//        okhttp3.Response response = null;
//        response = call.execute();
//
//        //Check if Call is successful
//        if (response.isSuccessful()) {
//            //Get Okhttp Response Code
//            int respCode = response.code();
//            logger.info("Code: {}" ,respCode);
//
//            //Get OkHttp Message
//            String respMessage = response.message();
//            logger.info("Message: {}" ,respMessage);
//
//            //Get OkHttp Resp Body
//            respBody = response.body().string();
//            logger.info("Response Body: {} ",respBody);
//
//            res = gson.fromJson(respBody, Response.class);
//        }else {
//            //Get OkHttp Resp Body
//            respBody = response.body().string();
//            logger.info("Response Body: {}"  , respBody);
//
//            //Get Okhttp Response Code
//            int respCode = response.code();
//            logger.info("Code: {}" , respCode);
//
//            //Get OkHttp Message
//            String respMessage = response.message();
//            logger.info("Message: {}", respMessage);
//
//            res = gson.fromJson(respBody, Response.class);
//        }
//        count++;
//        return respBody;
//    }

    public String  NotifyInstitution(TerminalTransactions transactionRecords) throws IOException, BadPaddingException, IllegalBlockSizeException, JSONException, DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Gson gson = new Gson() ;
        String respBody;
        Response res = null;
        int count = 0;
        pushWithdrawalPTSPRequest pushWithdrawalPTSPRequest = new pushWithdrawalPTSPRequest();
        Institution institution = institutionRepository.findByInstitutionID(transactionRecords.getInstitutionID());

        String url = institution.getInstitutionURL() + institution.getInstitutionIntegrationVersion();
        logger.info("Notification URL is {}",url);
        String TID = transactionRecords.getTerminalID();
        String bankcode = TID.substring(0,4);
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
        pushWithdrawalPTSPRequest.setProductId(transactionRecords.getProcessedBy());
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
        pushWithdrawalPTSPRequest.setProductId("3LINE001");
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


package com.jayrush.springmvcrest.Notification;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Repositories.bankServiceRepo;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.bank;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import com.jayrush.springmvcrest.domain.hostResponse;
import okhttp3.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

@Service
public class MedusaNotification {
    @Autowired
    TerminalRepository terminalRepository;
    @Autowired
    bankServiceRepo bankServiceRepo;
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(MedusaNotification.class);

    public void  NotifyInstitution(TerminalTransactions transactionRecords) throws IOException, BadPaddingException, IllegalBlockSizeException, JSONException, DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Gson gson = new Gson() ;
        final String[] respBody = {""};
        final Response[] res = {null};
        int count = 0;
        pushWithdrawalPTSPRequest pushWithdrawalPTSPRequest = new pushWithdrawalPTSPRequest();
        Institution institution = institutionRepository.findByInstitutionID(transactionRecords.getInstitutionID());

        String url = institution.getInstitutionURL() + institution.getInstitutionIntegrationVersion();
//        String url = "http://10.9.8.50:8080/integration/8MNMZCE1BG/api/v1/pos/transaction/log/";
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

//        client.readTimeoutMillis();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()){
                    throw new IOException("response unsuccessful");
                }else {
                    int respCode = response.code();
                    logger.info("Code: {}" ,respCode);
                    String respMessage = response.message();
                    logger.info("Message: {}" ,respMessage);
                    respBody[0] = response.body().string();
                    logger.info("Response Body: {} ", respBody[0]);
                    res[0] = gson.fromJson(respBody[0], Response.class);
                    Gson g = new Gson();

                    hostResponse hostResponse = g.fromJson(respBody[0], hostResponse.class);

                    if (hostResponse.getRespCode().equals("00")){
                        TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                        transactionLog.setInstitutionResponseCode("00");
                        transactionLog.setInstitutionResponseDesc("Processed");
                        transactionLog.setProcessed(true);
                        transactionRepository.save(transactionLog);

                    }else {
                        TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                        transactionLog.setInstitutionResponseCode("00");
                        transactionLog.setInstitutionResponseDesc("Processed");
                        transactionLog.setProcessed(false);
                        transactionRepository.save(transactionLog);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                transactionLog.setInstitutionResponseCode("00");
                transactionLog.setInstitutionResponseDesc("Processed");
                transactionLog.setProcessed(false);
                transactionRepository.save(transactionLog);
            }

        });


//        return respBody[0];
    }

    public void  NotifySingleInstitution(TerminalTransactions transactionRecords) throws IOException, BadPaddingException, IllegalBlockSizeException, JSONException, DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Gson gson = new Gson() ;
        final String[] respBody = {""};
        final Response[] res = {null};
        int count = 0;
        pushWithdrawalPTSPRequest pushWithdrawalPTSPRequest = new pushWithdrawalPTSPRequest();
        Institution institution = institutionRepository.findByInstitutionID(transactionRecords.getInstitutionID());

        String url = institution.getInstitutionURL() + institution.getInstitutionIntegrationVersion();
//        String url = "http://10.9.8.50:8080/integration/8MNMZCE1BG/api/v1/pos/transaction/log/";
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

//        client.readTimeoutMillis();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()){
                    throw new IOException("response unsuccessful");
                }else {
                    int respCode = response.code();
                    logger.info("Code: {}" ,respCode);
                    String respMessage = response.message();
                    logger.info("Message: {}" ,respMessage);
                    respBody[0] = response.body().string();
                    logger.info("Response Body: {} ", respBody[0]);
                    res[0] = gson.fromJson(respBody[0], Response.class);
                    Gson g = new Gson();
                    hostResponse hostResponse = g.fromJson(String.valueOf(res[0]), hostResponse.class);

                    if (hostResponse.getRespCode().equals("00")){
                        TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                        transactionLog.setInstitutionResponseCode("00");
                        transactionLog.setInstitutionResponseDesc("Processed");
                        transactionLog.setProcessed(true);
                        transactionRepository.save(transactionLog);

                    }else {
                        TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                        transactionLog.setInstitutionResponseCode("00");
                        transactionLog.setInstitutionResponseDesc("Processed");
                        transactionLog.setProcessed(false);
                        transactionRepository.save(transactionLog);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                TerminalTransactions transactionLog = transactionRepository.findById(transactionRecords.getId()).get();
                transactionLog.setInstitutionResponseCode("00");
                transactionLog.setInstitutionResponseDesc("Processed");
                transactionLog.setProcessed(false);
                transactionRepository.save(transactionLog);
            }

        });

    }

    public static void main(String[]args) throws DecoderException {
        String appkey = "b697772320da7258386eb3b002833d13";
        byte[]key = Hex.decodeHex(appkey.toCharArray());
        System.out.println(key);
    }

}


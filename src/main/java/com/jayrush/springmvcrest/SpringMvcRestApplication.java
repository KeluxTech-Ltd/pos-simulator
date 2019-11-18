package com.jayrush.springmvcrest;

import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.nibssresponse;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.jpos.iso.AsciiHexInterpreter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;

import static com.globasure.nibss.tms.client.lib.utils.StringUtils.hexStringToByteArray;
import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;

@SpringBootApplication
public class SpringMvcRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMvcRestApplication.class, args);

    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String messagesent = "303230304632334334364431323945303832333030303030303030303030303030303231313635333939383334353130363832373237303031303030303030303030303030303031313131343232343634393030303135323232343634393131313432323036353431313035313030313030303444303030303030303030333034343334353339393833343531303638323732374432323036323231303031343237393430313135373337363830303939383232313231303143583831323130314C4130303030304148343846726565646F6D204E6574776F726B204167656E637920202020202020202020202020204C414E47353636333334394630313036413030303030303030303031394630323036303030303030303030303031394630333036303030303030303030303030394630393032303030323946313031323031313041373430303330323030303030303030303030303030303030303030303046463946313530323030303139463236303833334243373733343035353341463638394632373031383039463333303345304638433839463334303334343033303239463335303132323946333630323030423639463337303432303731313835373946343130343030303030303031394631413032303536363946314530383330333033303330333033303330333139353035303030303030383830303941303331393131313439433031303035463234303332323036333035463241303230353636354633343031303138323032333930303834303741303030303030303034313031303030363031303130313031375061796D656E742066726F6D206D706F7330313535313131303135313233343431303142343230304144323834323143383739333531413031353632363436304646353536313144373733323136323432433032343831413231383141423138394242";
//        System.out.println("From POS-----> "+messagesent);
//
//        byte[]result = hexStringToByteArray(messagesent);
//        TerminalTransactions request = parseResponse(result);
//        System.out.println(request);
//
//    }
//
//    private TerminalTransactions parseRequest(final byte[] message) throws IOException, ParseException {
//        final TerminalTransactions response = new TerminalTransactions();
//        nibssresponse nibssresponse = new nibssresponse();
//        IsoMessage isoMessage = new IsoMessage();
//        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
//        responseMessageFactory.addMessageTemplate(isoMessage);
//        responseMessageFactory.setAssignDate(true);
//        responseMessageFactory.setUseBinaryBitmap(false);
//        responseMessageFactory.setUseBinaryMessages(false);
//        responseMessageFactory.setEtx(-1);
//        responseMessageFactory.setIgnoreLastMissingField(false);
//        responseMessageFactory.setConfigPath("requests.xml");
//        IsoMessage responseMessage = null;
//        try {
//            responseMessage = responseMessageFactory.parseMessage(message, 0);
//            printIsoFields(responseMessage, "Response ====> ");
//        }
//        catch (Exception e2) {
//            return response;
//        }
//        response.setResponseCode(responseMessage.getObjectValue(39).toString());
//        if (responseMessage != null && responseMessage.hasField(4)) {
//            response.setMti("0210");
//            if (responseMessage.hasField(2)) {
//                response.setPan(responseMessage.getObjectValue(2).toString());
//            }
//            if (responseMessage.hasField(4)) {
//                response.setAmount(responseMessage.getObjectValue(4).toString());
//            }
//            if (responseMessage.hasField(7)) {
//                response.setDateTime(responseMessage.getObjectValue(7).toString());
//            }
//            if (responseMessage.hasField(11)) {
//                response.setStan(responseMessage.getObjectValue(11).toString());
//            }
//            if (responseMessage.hasField(12)) {
//                response.setTime(responseMessage.getObjectValue(12).toString());
//            }
//            if (responseMessage.hasField(13)) {
//                response.setDate(responseMessage.getObjectValue(13).toString());
//            }
//            if (responseMessage.hasField(39)) {
//                response.setResponseCode(responseMessage.getObjectValue(39).toString());
//                response.setResponseDesc(nibssresponse.ResponseCodeMap(response.getResponseCode()));
//            }
//            if (responseMessage.hasField(41)) {
//                response.setTerminalID(responseMessage.getObjectValue(41).toString());
//            }
//            if (responseMessage.hasField(42)) {
//                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
//            }
//        }
//        System.out.println("Response: {}"+ (Object)response.getResponseCode());
//        return response;
//
//    }
//    private TerminalTransactions parseResponse(final byte[] message) throws IOException, ParseException {
//        final TerminalTransactions response = new TerminalTransactions();
//        nibssresponse nibssresponse = new nibssresponse();
//        final IsoMessage isoMessage = null;
//        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
//        responseMessageFactory.addMessageTemplate(isoMessage);
//        responseMessageFactory.setAssignDate(true);
//        responseMessageFactory.setUseBinaryBitmap(false);
//        responseMessageFactory.setUseBinaryMessages(false);
//        responseMessageFactory.setEtx(-1);
//        responseMessageFactory.setIgnoreLastMissingField(false);
//        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
//        IsoMessage responseMessage = null;
//        try {
//            responseMessage = responseMessageFactory.parseMessage(message, 0);
//            printIsoFields(responseMessage, "Response ====> ");
//        }
//        catch (Exception e2) {
//            return response;
//        }
//        response.setResponseCode(responseMessage.getObjectValue(39).toString());
//        if (responseMessage != null && responseMessage.hasField(4)) {
//            response.setMti("0210");
//            if (responseMessage.hasField(2)) {
//                response.setPan(responseMessage.getObjectValue(2).toString());
//            }
//            if (responseMessage.hasField(4)) {
//                response.setAmount(responseMessage.getObjectValue(4).toString());
//            }
//            if (responseMessage.hasField(7)) {
//                response.setDateTime(responseMessage.getObjectValue(7).toString());
//            }
//            if (responseMessage.hasField(11)) {
//                response.setStan(responseMessage.getObjectValue(11).toString());
//            }
//            if (responseMessage.hasField(12)) {
//                response.setTime(responseMessage.getObjectValue(12).toString());
//            }
//            if (responseMessage.hasField(13)) {
//                response.setDate(responseMessage.getObjectValue(13).toString());
//            }
//            if (responseMessage.hasField(39)) {
//                response.setResponseCode(responseMessage.getObjectValue(39).toString());
//                response.setResponseDesc(nibssresponse.ResponseCodeMap(response.getResponseCode()));
//            }
//            if (responseMessage.hasField(41)) {
//                response.setTerminalID(responseMessage.getObjectValue(41).toString());
//            }
//            if (responseMessage.hasField(42)) {
//                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
//            }
//        }
//        System.out.println("Response: {}"+ (Object)response.getResponseCode());
//        return response;
//
//    }

}
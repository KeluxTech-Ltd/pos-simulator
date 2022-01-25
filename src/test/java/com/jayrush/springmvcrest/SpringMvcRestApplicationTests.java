//package com.jayrush.springmvcrest;
//
//import com.jayrush.springmvcrest.Nibss.factory.NibssRequestsFactory;
//import com.jayrush.springmvcrest.Nibss.models.store.OfflineCTMK;
//import com.jayrush.springmvcrest.Nibss.repository.DataStore;
//import com.jayrush.springmvcrest.Repositories.TerminalRepository;
//import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
//import com.jayrush.springmvcrest.domain.Terminals;
//import com.jayrush.springmvcrest.domain.domainDTO.host;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Objects;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class SpringMvcRestApplicationTests {
//    private static Logger logger = LoggerFactory.getLogger(SpringMvcRestApplication.class);
//
//    @Autowired
//    TerminalRepository  terminalRepository;
//
//    @Autowired
//    terminalKeysRepo terminalKeysRepo;
//
//    @Test
//    public void keyManagement() {
//        String terminalID = "2101CX82";
//        Terminals terminals = terminalRepository.findByterminalID(terminalID);
//        host host = new host();
//        if (Objects.nonNull(terminals)){
//            host.setHostIp(terminals.getProfile().getProfileIP());
//            host.setHostPort(terminals.getProfile().getPort());
//        }
//        DataStore dataStore1 = new DataStore() {
//            @Override
//            public void putString(String p0, String p1) {
//
//            }
//
//            @Override
//            public void putInt(String p0, int p1) {
//
//            }
//
//            @Override
//            public String getString(String p0) {
//                return null;
//            }
//
//            @Override
//            public int getInt(String p0) {
//                return 0;
//            }
//        };
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_HOST_ID, host.getHostIp());
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_PORT_DETAILS,String.valueOf(host.getHostPort()));
//
//        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, terminalID,terminalKeysRepo);
//        OfflineCTMK offlineCTMK = new OfflineCTMK();
//        //switch based on profile name
//        switch (terminals.getProfile().getProfileName()){
//            case "POSVAS":
//                //best convention is to get the zpk from the terminal profile
//                offlineCTMK.setComponentOne("3BB9648A624F32C17C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEC1AD668F7CBFEC4CE1301AD");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//            case "EPMS":
//                offlineCTMK.setComponentOne("3BB9648A62s4F3217C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEdC1AD68F7CBFEC4CE1301AD");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//            case "ISW":
//                offlineCTMK.setComponentOne("3BB9648A624F32C1C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEC1AD68F7CBFEC4CE1301AD");
//                //make route for interswitch
//                break;
//            default:
//                logger.info("No ZPK exists for profile");
//                break;
//        }
//
//
//
//
//
//    }
//
//    private static void getKeys_Params(NibssRequestsFactory factory, OfflineCTMK offlineCTMK, host host) {
//        if (!factory.getMasterKey(offlineCTMK,host)) {
//            logger.info("Failed to download Master Key");
//        }
//
//        logger.info("Master Key Downloaded");
//
//        if (!factory.getSessionKey(host)) {
//            logger.info("Failed to download Session Key");
//
//        }
//
//        if (!factory.getPinKey(host)) {
//            logger.info("Failed to download Pin Key");
//
//        }
//
//        if (!factory.getParameters(host)) {
//            logger.info("Failed to download Parameters");
//        }
//    }
//
//
//}
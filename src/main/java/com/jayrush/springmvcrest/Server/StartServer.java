package com.jayrush.springmvcrest.Server;

import com.jayrush.springmvcrest.CreateSocketServer;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
import com.jayrush.springmvcrest.rolesPermissions.repositories.rolesRepository;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import com.jayrush.springmvcrest.serviceProviders.repository.profilesServiceRepo;
import com.jayrush.springmvcrest.serviceProviders.repository.serviceProviderRepo;
import com.jayrush.springmvcrest.wallet.models.walletAccount;
import com.jayrush.springmvcrest.wallet.repository.walletAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.*;


@Component
public class StartServer implements CommandLineRunner { //command line starter simply says run me on startup
    private static Logger logger = LoggerFactory.getLogger(StartServer.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    TransactionInterface transactionInterface;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    rolesRepository rolesRepository;

    @Autowired
    profilesServiceRepo profilesServiceRepo;

    @Autowired
    serviceProviderRepo serviceProviderRepo;

    @Autowired
    permissionRepository permissionRepository;

    @Autowired
    walletAccountRepository walletAccountRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(StartServer.class);
        rolesPermission();
        SuperAdminCheck();
        ProvidersCheck();
        ThreeLineGlWalletAccount();

        logger.info("##############################################################################");
        logger.info("***********************************MEDUSA**********************************");
        logger.info("Awaiting Connection\n");

        while (true) {
            try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open())
            {
                CreateSocketServer createSocketServer = new CreateSocketServer();
                applicationContext.getAutowireCapableBeanFactory().autowireBean(createSocketServer);
                createSocketServer.createSocket();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private void SuperAdminCheck() {
        tmsUser user = userRepository.findByusername("SuperAdmin");

        Date date = new Date();
        if (Objects.isNull(user)){
            Roles role = rolesRepository.findByName("SUPER_ADMIN");
            logger.info("Creating SuperAdmin");
            tmsUser user1 = new tmsUser();
            user1.setInstitution(null);
            String password = "Admin@3line";
            user1.setPassword(passwordEncoder.encode(password));

            String username = "SuperAdmin";
            user1.setUsername(username);
            logger.info("Username = {}",username);
            logger.info("Password = {}",password);
            user1.setFirstname("3Line");
            user1.setLastname("Card Management");
            user1.setEmail("Software@3lineng.com");
            user1.setDatecreated(date.toString());
            user1.setRole(role);
            user1.setChangePassword(false);
            userRepository.save(user1);
        }
        else {
            logger.info("SuperAdmin user found");
        }
    }

    private void ProvidersCheck(){
        List<serviceProviders> serviceProviders = serviceProviderRepo.findAll();
        if (serviceProviders.size()==0){
            profiles profiles1 = new profiles();
            profiles profiles2 = new profiles();
            profiles profiles3 = new profiles();

            profiles1.setServiceProviders(null);
            profiles1.setZpk("5f28c6358e55e4a98b8bc90cd4c3b466");
            profiles1.setPort(5043);
            profiles1.setProfileName("EPMS");
            profiles1.setProfileIP("196.6.103.73");
            profiles1.setId(1L);

            profiles2.setServiceProviders(null);
            profiles2.setZpk("5fc0d4cae4229ac042c35cfc18a6a539");
            profiles2.setPort(5009);
            profiles2.setProfileName("POSVAS");
            profiles2.setProfileIP("196.6.103.18");
            profiles2.setId(2L);

            profiles3.setServiceProviders(null);
            profiles3.setZpk("null");
            profiles3.setPort(7003);
            profiles3.setProfileName("ISW");
            profiles3.setProfileIP("10.2.2.65");
            profiles3.setId(3L);
            profilesServiceRepo.save(profiles1);
            profilesServiceRepo.save(profiles2);
            profilesServiceRepo.save(profiles3);

            List<profiles>profilesList = new ArrayList<>();
            profilesList.add(0,profiles1);
            profilesList.add(1,profiles2);

            List<profiles>profilesList2 = new ArrayList<>();
            profilesList.add(0,profiles3);

            serviceProviders serviceProviders1 = new serviceProviders();
            serviceProviders serviceProviders2 = new serviceProviders();
            serviceProviders1.setSaved(true);
            serviceProviders1.setSavedDescription("Saved Successfully");
            serviceProviders1.setProfile(profilesList);
            serviceProviders1.setProviderName("NIBSS");
            serviceProviders1.setId(1L);
            serviceProviderRepo.save(serviceProviders1);

            serviceProviders2.setSaved(true);
            serviceProviders2.setSavedDescription("Saved Successfully");
            serviceProviders2.setProfile(profilesList2);
            serviceProviders2.setProviderName("ISW");
            serviceProviders1.setId(2L);
            serviceProviderRepo.save(serviceProviders2);

            profilesServiceRepo.save(profiles1);
            profilesServiceRepo.save(profiles2);
            profilesServiceRepo.save(profiles3);
        }
        else {
            logger.info("Service and profiles already exists");
        }

    }

    private void rolesPermission(){
        Roles role1 = rolesRepository.findByName("SUPER_ADMIN");
        if (Objects.isNull(role1)){
            Permissions permissions = new Permissions();
            permissions.setName("CREATE_INSTITUTION");
            permissions.setDescription("User can create institution on Medusa");
            permissionRepository.save(permissions);

            Permissions permissions1 = new Permissions();
            permissions1.setName("ADD_TERMINALS");
            permissions1.setDescription("User can create terminals on Medusa");
            permissionRepository.save(permissions1);

            Permissions permissions2 = new Permissions();
            permissions2.setName("GLOBAL_SETTINGS");
            permissions2.setDescription("User can toggle Global settings on Medusa");
            permissionRepository.save(permissions2);

            Permissions permissions3 = new Permissions();
            permissions3.setName("CREATE_ROLES");
            permissions3.setDescription("User can create roles on Medusa");
            permissionRepository.save(permissions3);

            Permissions permissions4 = new Permissions();
            permissions4.setName("CREATE_PROVIDERS");
            permissions4.setDescription("User can create service providers on Medusa");
            permissionRepository.save(permissions4);

            Permissions permissions5 = new Permissions();
            permissions5.setName("CREATE_PROFILES");
            permissions5.setDescription("User can create service profiles on Medusa");
            permissionRepository.save(permissions5);

            Permissions permissions6 = new Permissions();
            permissions6.setName("CREATE_WALLET");
            permissions6.setDescription("User can create institution wallet on Medusa");
            permissionRepository.save(permissions6);

            Permissions permissions7 = new Permissions();
            permissions7.setName("CREATE_USER");
            permissions7.setDescription("User can create institution user on Medusa");
            permissionRepository.save(permissions7);

            Permissions permissions8 = new Permissions();
            permissions8.setName("UPDATE_WALLET");
            permissions8.setDescription("User can update institution wallet on Medusa");
            permissionRepository.save(permissions8);

            Roles roles = new Roles();
            Roles role2 = new Roles();
            Roles role3 = new Roles();
            Collection<Permissions> permissionsCollection =permissionRepository.findAll();
            roles.setPermissions(permissionsCollection);
            roles.setDescription("Medusa SuperAdmin Role");
            roles.setName("SUPER_ADMIN");
            roles.setInstitution(null);
            rolesRepository.save(roles);
            Collection<Permissions> permissionsCollection2 =new ArrayList<>();
            permissionsCollection2.add(permissions1);
            permissionsCollection2.add(permissions2);
            permissionsCollection2.add(permissions7);
            role2.setPermissions(permissionsCollection2);
            role2.setDescription("Medusa Admin Role");
            role2.setName("ADMIN");
            role2.setInstitution(null);
            rolesRepository.save(role2);
            Collection<Permissions> permissionsCollection3 =new ArrayList<>();
            permissionsCollection3.add(permissions1);
            permissionsCollection3.add(permissions2);
            permissionsCollection3.add(permissions3);
            permissionsCollection3.add(permissions7);
            role3.setPermissions(permissionsCollection3);
            role3.setDescription("Institution Admin Role");
            role3.setName("INSTITUTION_ADMIN");
            role3.setInstitution(null);
            rolesRepository.save(role3);
        }
    }

    private void ThreeLineGlWalletAccount() {
        walletAccount walletAccount = walletAccountRepository.findByWalletNumber("3lineWallet");
        Date date = new Date();
        if (Objects.isNull(walletAccount)){
            walletAccount walletAccount1 = new walletAccount();
            walletAccount1.setMaximumCharge(15.00);
            walletAccount1.setMinimumCharge(10.00);
            walletAccount1.setFeePercentage(0.003);
            walletAccount1.setPurpose("3LINE GL ACCOUNT");
            walletAccount1.setLastTranDate(date);
            walletAccount1.setLedgerBalance(0.00);
            walletAccount1.setAvailableBalance(0.00);
            walletAccount1.setWalletNumber("3lineWallet");
            walletAccount1.setDeletedOn(null);
            walletAccount1.setDelFlag("N");
            walletAccount1.setIsGeneralLedger(null);
            walletAccountRepository.save(walletAccount1);
            logger.info("3Line GL Account created");
        }
        else {
            logger.info("3Line GL Account found");
        }
        walletAccount walletAccount2 = walletAccountRepository.findByWalletNumber("Medusa_Income_Wallet");
        if (Objects.isNull(walletAccount2)){
            walletAccount walletAccount3 = new walletAccount();
            walletAccount3.setMaximumCharge(0.0);
            walletAccount3.setMinimumCharge(0.0);
            walletAccount3.setFeePercentage(0.0);
            walletAccount3.setPurpose("Medusa_Income_Wallet");
            walletAccount3.setLastTranDate(date);
            walletAccount3.setLedgerBalance(0.0);
            walletAccount3.setAvailableBalance(0.0);
            walletAccount3.setWalletNumber("Medusa_Income_Wallet");
            walletAccount3.setDeletedOn(null);
            walletAccount3.setDelFlag("N");
            walletAccount3.setIsGeneralLedger(null);
            walletAccountRepository.save(walletAccount3);
            logger.info("Medusa Income Wallet Account created");
        }
        else {
            logger.info("Medusa Income Wallet Account found");
        }
    }

}

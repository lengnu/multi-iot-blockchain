package com.multi.domain.iot.ud.service;

import com.multi.domain.iot.ud.config.SystemConfig;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Slf4j
public class ServiceManager {
    @Autowired
    private SystemConfig config;

    @Autowired
    private Client client;

    List<String> hexPrivateKeyList;

    @PostConstruct
    public void init() {
        hexPrivateKeyList = Arrays.asList(this.config.getHexPrivateKey().split(","));
    }

    /**
     * @notice: must use @Qualifier("AuthenticationService") with @Autowired to get this Bean
     */
    @Bean("AuthenticationService")
    public Map<String, AuthenticationService> initAuthenticationServiceManager() throws Exception {
        Map<String, AuthenticationService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
        for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
            String privateKey = this.hexPrivateKeyList.get(i);
            if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
                privateKey = privateKey.substring(2);
            }
            if (privateKey.isEmpty()) {
                continue;
            }
            org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
            org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
            String userAddress = cryptoKeyPair.getAddress();
            //	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
            AuthenticationService authenticationService = new AuthenticationService();
            authenticationService.setAddress(this.config.getContract().getAuthenticationAddress());
            authenticationService.setClient(this.client);
            org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor =
                    org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
            authenticationService.setTxProcessor(txProcessor);
            serviceMap.put(userAddress, authenticationService);
        }
        //log.info("++++++++AuthenticationService map:{}", serviceMap);
        return serviceMap;
    }
}

package com.multi.domain.iot.ud.service;

import com.multi.domain.iot.ud.model.bo.AuthenticationAddAuthenticationInformationInputBO;
import com.multi.domain.iot.ud.model.bo.AuthenticationIsAuthorizedInputBO;

import java.lang.Exception;
import java.lang.String;
import java.math.BigInteger;
import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Data
public class AuthenticationService {
    public static final String ABI = com.multi.domain.iot.ud.utils.IOUtil.readFileAsString("abi/Authentication.abi");

    public static final String BINARY = com.multi.domain.iot.ud.utils.IOUtil.readFileAsString("bin/ecc/Authentication.bin");

    @Value("${system.contract.authenticationAddress}")
    private String address;

    @Autowired
    private Client client;

    AssembleTransactionProcessor txProcessor;

    @PostConstruct
    public void init() throws Exception {
        this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
    }

    public CallResponse isAuthorized(AuthenticationIsAuthorizedInputBO input) throws Exception {
        return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "isAuthorized", input.toArgs());
    }

    public TransactionResponse addAuthenticationInformation(AuthenticationAddAuthenticationInformationInputBO input) throws Exception {
        return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "addAuthenticationInformation", input.toArgs());
    }

    /**
     * 判断对应的PID是否被授权
     */
    public boolean isAuthorized(String pid) throws Exception {
        AuthenticationIsAuthorizedInputBO bo = new AuthenticationIsAuthorizedInputBO();
        bo.setPid(pid);
        CallResponse response = this.isAuthorized(bo);
        Boolean result = (Boolean) response.getReturnObject().get(0);
        return result;
    }

    /**
     * pid上链
     */
    public boolean addAuthenticationInformation(String pid, String identityProtectionInformation) throws Exception {
        AuthenticationAddAuthenticationInformationInputBO bo = new AuthenticationAddAuthenticationInformationInputBO();
        bo.setPid(pid);
        bo.setIdentityProtectionInformation(identityProtectionInformation);
        TransactionResponse transactionResponse = addAuthenticationInformation(bo);
        BigInteger result = (BigInteger) transactionResponse.getReturnObject().get(0);
        return result.equals(BigInteger.ONE);
    }
}

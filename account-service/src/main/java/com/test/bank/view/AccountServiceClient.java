/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.bank.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.bank.accountservice.dto.*;
import com.test.bank.view.exception.ClientException;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountServiceClient {

    private static final String FIND_ACCOUNT_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts";
    private static final String FIND_ACCOUNT_BY_ID_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts/{id}";
    private static final String GET_CURRENT_BALANCE_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts/{id}/balance";
    private static final String OPEN_ACCOUNT_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts";
    private static final String CLOSE_ACCOUNT_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts/{id}/close";
    private static final String MAKE_DEPOSIT_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts/{id}/deposit";
    private static final String MAKE_WITHDRAWAL_ENDPOINT_URL = "http://localhost:8080/api/v1/accounts/{id}/withdrawal";
    private static RestTemplate restTemplate = new RestTemplate();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public AccountDetailDTO findAccountById(Long accountId) throws ClientException {
        try{
            final Map<String, Long> params = new HashMap<>();
            params.put("id", accountId);
            return restTemplate.getForObject(FIND_ACCOUNT_BY_ID_ENDPOINT_URL, AccountDetailDTO.class, params);
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public AccountDTO authenticate(String accountNumber, String pin) throws ClientException {
        final UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(FIND_ACCOUNT_ENDPOINT_URL)
                .queryParam("accountNumber", accountNumber)
                .queryParam("pin", pin);
        try{
            return restTemplate.getForObject(urlBuilder.toUriString(), AccountDTO.class);
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public AccountBalanceDTO getCurrentBalanceForAccount(Long accountId) throws ClientException{
        final Map<String, Long> params = new HashMap<>();
        params.put("id", accountId);
        try{
            return restTemplate.getForObject(GET_CURRENT_BALANCE_ENDPOINT_URL, AccountBalanceDTO.class, params);
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public ResponseDTO openNewAccount(AccountRequestDTO requestDTO) throws ClientException{
        try {
            return restTemplate.postForObject(OPEN_ACCOUNT_ENDPOINT_URL, requestDTO, ResponseDTO.class);
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public ResponseDTO closeAccount(Long accountId) throws ClientException{
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        final Map<String, Long> params = new HashMap<>();
        params.put("id", accountId);

        try {
            final ResponseEntity<ResponseDTO> result = restTemplate.exchange(CLOSE_ACCOUNT_ENDPOINT_URL,
                    HttpMethod.PUT,entity,
                    ResponseDTO.class, params);
            return result.getBody();
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public ResponseDTO makeWithdrawal(Long accountId, TransactionRequestDTO requestDTO) throws ClientException{
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        final HttpEntity<TransactionRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        final Map<String, Long> params = new HashMap<>();
        params.put("id", accountId);

        try {
            final ResponseEntity<ResponseDTO> result = restTemplate.exchange(MAKE_WITHDRAWAL_ENDPOINT_URL,
                    HttpMethod.PUT,entity,
                    ResponseDTO.class, params);
            return result.getBody();
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }

    public ResponseDTO makeDeposit(Long accountId, TransactionRequestDTO requestDTO) throws ClientException{
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        final HttpEntity<TransactionRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        final Map<String, Long> params = new HashMap<>();
        params.put("id", accountId);

        try {
            final ResponseEntity<ResponseDTO> result = restTemplate.exchange(MAKE_DEPOSIT_ENDPOINT_URL,
                    HttpMethod.PUT,entity,
                    ResponseDTO.class, params);
            return result.getBody();
        } catch (HttpClientErrorException e){
            throw handleErrorAndCreateClientException(e);
        } catch (Exception e){
            throw handleGeneralException(e);
        }
    }


    private ClientException handleErrorAndCreateClientException(HttpClientErrorException e) {
        final MessageResponseDTO messageResponseDTO = mapToObject(e.getResponseBodyAsString(), MessageResponseDTO.class);
        return new ClientException(messageResponseDTO.getMessage());
    }

    private ClientException handleGeneralException(Exception e) {
        return new ClientException("There was something wrong in the system, please try again");
    }

    private <T> T mapToObject(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}

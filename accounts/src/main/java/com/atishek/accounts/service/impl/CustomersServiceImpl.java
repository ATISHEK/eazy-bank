package com.atishek.accounts.service.impl;

import com.atishek.accounts.dto.AccountsDto;
import com.atishek.accounts.dto.CardsDto;
import com.atishek.accounts.dto.CustomerDetailsDto;
import com.atishek.accounts.dto.LoansDto;
import com.atishek.accounts.entity.Accounts;
import com.atishek.accounts.entity.Customer;
import com.atishek.accounts.exception.ResourceNotFoundException;
import com.atishek.accounts.mapper.AccountsMapper;
import com.atishek.accounts.mapper.CustomerMapper;
import com.atishek.accounts.repository.AccountsRepository;
import com.atishek.accounts.repository.CustomerRepository;
import com.atishek.accounts.service.ICustomersService;
import com.atishek.accounts.service.client.CardsFeignClient;
import com.atishek.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     *  @param correlationId - Correlation ID value generated at Edge server
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if(Objects.isNull(loansDtoResponseEntity)) {
            customerDetailsDto.setLoansDto(null);
        }else  customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if(Objects.isNull(cardsDtoResponseEntity)) {
            customerDetailsDto.setCardsDto(null);
        }else  customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}

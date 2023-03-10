package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    public Boolean isPresent(Long chatId) {
        return this.customerRepository.findCustomerByChatId(chatId).isPresent();
    }
    
    
    
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    public Customer createCustomer(Long chatId) {
        Customer customerRecord = new Customer();
        customerRecord.setChatId(chatId);
        return customerRepository.save(customerRecord);
    }

    /*
    private Customer getCustomerById(Long id) {
        return customerRepository.findCustomerById(id).orElse(null);
    }
    */
    public Customer updateCustomer(Customer customer) {
        if (findCustomerById(customer.getId()) == null) {
            return null;
        } else {
            return customerRepository.save(customer);
        }
    }

    public Customer deleteCustomerById(Long id) {
        Customer customer = findCustomerById(id);
        if (customer == null) {
            return null;
        } else {
            customerRepository.deleteById(id);
            return customer;
        }
    }

    public Customer findCustomerByChatId(Long id) {
        return customerRepository.findCustomerByChatId(id).orElse(null);
    }

}

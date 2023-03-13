package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static ga.heaven.model.CustomerContext.Context.FREE;

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
        if (null == customer.getCustomerContext()) {
            CustomerContext customerContext = new CustomerContext();
            customerContext.setDialogContext(FREE);
            customer.setCustomerContext(customerContext);
        }
        return customerRepository.save(customer);
    }
    
    public Customer createCustomer(Long chatId) {
        Customer customerRecord = new Customer();
        customerRecord.setChatId(chatId);
        CustomerContext customerContext = new CustomerContext();
        customerContext.setDialogContext(FREE);
        customerRecord.setCustomerContext(customerContext);
        return customerRepository.save(customerRecord);
    }

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

package ga.heaven.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.repository.CustomerRepository;
import ga.heaven.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ga.heaven.model.CustomerContext.Context.FREE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CustomerController customerController;

    @SpyBean
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private BreedService breedService;
    @MockBean
    private InfoService infoService;
    @MockBean
    private PeripheralService peripheralService;

    @MockBean
    private PetService petService;
    @MockBean
    private ReportService reportService;
    @MockBean
    private ShelterService shelterService;

    @MockBean
    private VolunteerService volunteerService;

    private Gson gson = new Gson();

    private List<Customer> getInitialTestCustomers() {
        Customer customer1 = getTestCustomer(1L, "customerName1", "+71111111111");
        Customer customer2 = getTestCustomer(2L, "customerName2", "+72222222222");

        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);

        return customers;
    }

    private Customer getTestCustomer(long id, String name, String phoneNumber) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setPhone(phoneNumber);
        return customer;
    }

    @Test
    public void getAllCustomers() throws Exception{
        List<Customer> customers = getInitialTestCustomers();
        when(customerRepository.findAll()).thenReturn(customers);

        MockHttpServletResponse response = mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Type listOfMyClassObject = new TypeToken<ArrayList<Customer>>() {}.getType();

        List<Customer> actual = gson.fromJson(response.getContentAsString(), listOfMyClassObject);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(customers);
        assertThat(actual).isEqualTo(customers);
    }

    @Test
    void getCustomerById() throws Exception{
        Customer customer = getTestCustomer(25L, "Иван", "+71111111111");
        when(customerRepository.findById(25L)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(35L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/35"))
                .andExpect(status().isNotFound());

        MockHttpServletResponse response = mockMvc.perform(get("/customer/25"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Customer actual = gson.fromJson(response.getContentAsString(), Customer.class);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void createCustomerPositive() throws Exception {
        Customer expectedCustomer = getTestCustomer(25L, "Иван", "+71111111111");
        when(customerRepository.save(expectedCustomer)).thenReturn(expectedCustomer);

        JSONObject jo = new JSONObject();
        jo.put("id", 25L);
        jo.put("name", "Иван");
        jo.put("phone", "+71111111111");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/customer")
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedCustomer.getId()))
                .andReturn().getResponse();
        Customer actual = gson.fromJson(response.getContentAsString(), expectedCustomer.getClass());
        assertThat(actual).isEqualTo(expectedCustomer);
    }

    @Test
    void updateCustomerPositive() throws Exception {
        CustomerContext expectedCustomerContext = new CustomerContext(1L, FREE, null, null);
        Customer expectedCustomer = new Customer(1L, 333_333_333L, "Petrov", "Ivan", "Frolovich", "+71111111111", "address", expectedCustomerContext);

        when(customerRepository.findCustomerById(expectedCustomer.getId())).thenReturn(Optional.of(expectedCustomer));
        when(customerRepository.findById(expectedCustomer.getId())).thenReturn(Optional.of(expectedCustomer));
        when(customerRepository.save(expectedCustomer)).thenReturn(expectedCustomer);

        JSONObject joc = new JSONObject();
        joc.put("id", expectedCustomer.getCustomerContext().getId());
        joc.put("dialogContext", expectedCustomer.getCustomerContext().getDialogContext());
        joc.put("currentPetId", expectedCustomer.getCustomerContext().getCurrentPetId());

        JSONObject jo = new JSONObject();
        jo.put("id", expectedCustomer.getId());
        jo.put("chatId", expectedCustomer.getChatId());
        jo.put("surname", expectedCustomer.getSurname());
        jo.put("name", expectedCustomer.getName());
        jo.put("secondName", expectedCustomer.getSecondName());
        jo.put("phone", expectedCustomer.getPhone());
        jo.put("address", expectedCustomer.getAddress());
        jo.put("customerContext", joc);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put("/customer")
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedCustomer.getId()))
                .andReturn().getResponse();
        Customer actual = gson.fromJson(response.getContentAsString(), expectedCustomer.getClass());
        assertThat(actual).isEqualTo(expectedCustomer);
    }

    @Test
    void updateCustomerNegative() throws Exception {
        Customer expectedCustomer = new Customer();
        expectedCustomer.setId(25L);
        when(customerRepository.findById(expectedCustomer.getId())).thenReturn(Optional.empty());

        JSONObject newCustomerJson = new JSONObject();
        newCustomerJson.put("id", expectedCustomer.getId());
        newCustomerJson.put("name", "Ivan");
        newCustomerJson.put("phone", "+71111111111");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put("/customer")
                        .content(newCustomerJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();
        Customer actual = gson.fromJson(response.getContentAsString(), expectedCustomer.getClass());
        assertThat(actual).isNull();
    }

    @Test
    void removeCustomer() throws Exception{
        when(customerRepository.findById(35L)).thenReturn(Optional.empty()); // нет такого

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/customer/35")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Customer customer = getTestCustomer(25L, "Иван", "+71111111111");
        when(customerRepository.findById(25L)).thenReturn(Optional.of(customer));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/customer/25")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Customer actual = gson.fromJson(response.getContentAsString(), Customer.class);
        assertThat(actual).isEqualTo(customer);
    }
}
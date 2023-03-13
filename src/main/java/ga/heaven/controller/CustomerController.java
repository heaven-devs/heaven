package ga.heaven.controller;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("customer")
@RestController
@Tag(name = "\uD83D\uDE4B Customer store", description = "Customer dependence model CRUD endpoints")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Gets all records",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Customer.class)
                            )
                    ),
            },
            tags = "\uD83D\uDE4B Customer store"
    )
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }
    
    @Operation(
            summary = "Gets record by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Customer.class)
                            )
                    ),
            },
            tags = "\uD83D\uDE4B Customer store"
    )
    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.findCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customer);
        }
    }
    
    @Operation(
            summary = "Creates record",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Customer.class)
                            )
                    ),
            },
            tags = "\uD83D\uDE4B Customer store"
    )
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }
    
    @Operation(
            summary = "Edits records",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Customer.class)
                            )
                    ),
            },
            tags = "\uD83D\uDE4B Customer store"
    )
    @PutMapping
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer newCustomer) {
        Customer customer = customerService.updateCustomer(newCustomer);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customer);
        }
    }
    
    @Operation(
            summary = "Deletes records",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Customer.class)
                            )
                    ),
            },
            tags = "\uD83D\uDE4B Customer store"
    )
    @DeleteMapping("{id}")
    public ResponseEntity<Customer> removeCustomer(@PathVariable Long id) {
        Customer customer = customerService.deleteCustomerById(id);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(customer);
    }

}

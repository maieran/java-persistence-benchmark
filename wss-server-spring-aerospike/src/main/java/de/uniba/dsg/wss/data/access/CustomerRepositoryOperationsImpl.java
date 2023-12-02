package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.AddressData;
import de.uniba.dsg.wss.data.model.CustomerData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link CustomerRepositoryOperations} interface for
 * accessing and modifying {@link CustomerData customers}.
 *
 * @author Andre Maier
 */
public class CustomerRepositoryOperationsImpl implements CustomerRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public CustomerRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, CustomerData> idsToCustomers) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToCustomers.forEach((id, customer) -> aerospikeTemplate.save(customer));
  }

  @Override
  public List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds) {
    List<CustomerData> customers = new ArrayList<>();
    // 1.Step - Collect the keys/ids necessary to retrieve the objects
    Key[] keys = new Key[customerRefsIds.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(CustomerData.class),
              customerRefsIds.get(i));
    }

    // 2.Step - Retrieve orderItemData from Aerospike data model
    Record[] records = aerospikeTemplate.getAerospikeClient().get(null, keys);

    // 3.Step - Populate the list of orderItems
    for (int i = 0; i < records.length; i++) {
      Record record = records[i];
      if (record != null) {

        // Manually map the bins to CustomerData

        Map<String, Object> addressMap = (Map<String, Object>) record.getMap("address");

        AddressData addressData =
            new AddressData(
                (String) addressMap.get("street1"),
                (String) addressMap.get("street2"),
                (String) addressMap.get("zipCode"),
                (String) addressMap.get("city"),
                (String) addressMap.get("state"));

        // Create the OrderData instance
        CustomerData customer =
            new CustomerData(
                customerRefsIds.get(i), // Set the id using the itemsIds list
                record.getString("firstName"),
                record.getString("middleName"),
                record.getString("lastName"),
                addressData,
                record.getString("phoneNumber"),
                record.getString("email"),
                record.getString("districtRefId"),
                convertEntryDate(record.getLong("since")),
                record.getString("credit"),
                record.getDouble("creditLimit"),
                record.getDouble("discount"),
                record.getDouble("amount"),
                record.getDouble("ytdPayment"),
                record.getInt("paymentCount"),
                record.getInt("deliveryCount"),
                record.getString("data"));
        customer.setOrderRefsIds((Map<String, String>) record.getMap("orderRefsIds "));
        customer.setPaymentRefsIds((List<String>) record.getList("paymentRefsIds"));

        customers.add(customer);
      }
    }

    return customers;
  }

  private LocalDateTime convertEntryDate(Long entryDateMillis) {
    if (entryDateMillis != null) {
      Instant instant = Instant.ofEpochMilli(entryDateMillis);
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    return null;
  }

  /*  @Override
  public List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds) {
    List<CustomerData> customers = new ArrayList<>();

    for (String id : customerRefsIds) {
      // Read the record for the key
      CustomerData customer = aerospikeTemplate.findById(id, CustomerData.class);

      // Check if the record exists
      if (customer != null) {
        customers.add(customer);
      }
    }

    return customers;
  }*/

  @Override
  public void storeUpdatedCustomer(Optional<CustomerData> customer) {
    aerospikeTemplate.update(customer.get());
  }

  /*  @Override
  public Map<String, CustomerData> getCustomers() {
    return aerospikeTemplate
        .findAll(CustomerData.class)
        .collect(Collectors.toMap(CustomerData::getId, customer -> customer));
  }*/

  @Override
  public Map<String, CustomerData> getCustomers() {
    BatchPolicy batchPolicy = new BatchPolicy();
    int timeout_socket = 1800000;
    int timeout_read = 1800000;
    batchPolicy.setTimeouts(timeout_socket, timeout_read);

    // Retrieve all keys of CustomerData from Aerospike
    Set<String> customerIds =
        aerospikeTemplate
            .findAll(CustomerData.class)
            .map(CustomerData::getId)
            .collect(Collectors.toSet());

    Key[] keys =
        customerIds.stream()
            .map(
                id ->
                    new Key(
                        aerospikeTemplate.getNamespace(),
                        aerospikeTemplate.getSetName(CustomerData.class),
                        id))
            .toArray(Key[]::new);

    Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    Map<String, CustomerData> customers =
        IntStream.range(0, records.length)
            .parallel()
            .filter(i -> records[i] != null)
            .mapToObj(
                i -> {
                  Record record = records[i];
                  String customerId = customerIds.stream().skip(i).findFirst().orElse(null);

                  Map<String, Object> addressMap = (Map<String, Object>) record.getMap("address");

                  AddressData addressData =
                      new AddressData(
                          (String) addressMap.get("street1"),
                          (String) addressMap.get("street2"),
                          (String) addressMap.get("zipCode"),
                          (String) addressMap.get("city"),
                          (String) addressMap.get("state"));

                  CustomerData customer =
                      new CustomerData(
                          customerId, // Set the id using the customerIds set
                          record.getString("firstName"),
                          record.getString("middleName"),
                          record.getString("lastName"),
                          addressData,
                          record.getString("phoneNumber"),
                          record.getString("email"),
                          record.getString("districtRefId"),
                          convertEntryDate(record.getLong("since")),
                          record.getString("credit"),
                          record.getDouble("creditLimit"),
                          record.getDouble("discount"),
                          record.getDouble("balance"),
                          record.getDouble("ytdPayment"),
                          record.getInt("paymentCount"),
                          record.getInt("deliveryCount"),
                          record.getString("data"));
                  customer.setOrderRefsIds((Map<String, String>) record.getMap("orderRefsIds"));
                  customer.setPaymentRefsIds((List<String>) record.getList("paymentRefsIds"));

                  return customer;
                })
            .collect(Collectors.toMap(CustomerData::getId, customer -> customer));

    return customers;
  }
}

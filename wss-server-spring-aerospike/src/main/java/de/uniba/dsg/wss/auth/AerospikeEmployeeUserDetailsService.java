package de.uniba.dsg.wss.auth;

import de.uniba.dsg.wss.data.access.EmployeeRepository;
import de.uniba.dsg.wss.data.model.EmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AerospikeEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public AerospikeEmployeeUserDetailsService(
      AuthorityMapping authorityMapping, EmployeeRepository employeeRepository) {
    super(authorityMapping);
    this.employeeRepository = employeeRepository;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeData employee = employeeRepository.findEmployeeDataByUsername(username);
    if (employee == null) {
      throw new UsernameNotFoundException("Unable to find user with name " + username);
    }

    return createUserDetails(employee.getUsername(), employee.getPassword(), employee.getRole());
  }
}

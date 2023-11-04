package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setConnected(false);
        user.setMaskedIp(null);
        Country country = null;
        List<Country> countrylist = countryRepository3.findAll();
        for(Country c : countrylist){
            if(c.getCountryName().equals(countryName)){
                country = c;
            }
        }

        user.setCountry(country);


        int userId = user.getId();
        CountryName validCountry ;

        try {
            validCountry=CountryName.valueOf(countryName);
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
        String countryCode = validCountry.toCode();

        String originalIp = countryCode +"."+userId;

        user.setOriginalIp(originalIp);

        country.setUser(user);
        countryRepository3.save(country);

        userRepository3.save(user);

        return user;






    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {

        Optional<User> userOptional = userRepository3.findById(userId);
        Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository3.findById(serviceProviderId);
        User user = null;
        ServiceProvider serviceProvider = null;
        if(userOptional.isPresent() && serviceProviderOptional.isPresent()){
            user = userOptional.get();
            serviceProvider = serviceProviderOptional.get();
        }
        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        user.setServiceProviderList(serviceProviderList);

        List<User> userList = serviceProvider.getUsers();
        userList.add(user);
        serviceProvider.setUsers(userList);

        userRepository3.save(user);
        serviceProviderRepository3.save(serviceProvider);

        return  user;
    }
}

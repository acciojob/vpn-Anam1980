package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setPassword(password);
        admin.setUsername(username);

        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Optional<Admin>adminOptional = adminRepository1.findById(adminId);
        Admin admin = null;
        if(adminOptional.isPresent()){
            admin = adminOptional.get();
            List<ServiceProvider> serviceProviderList = admin.getServiceProviders();
            ServiceProvider serviceProvider = new ServiceProvider();
            serviceProvider.setName(providerName);
            serviceProvider.setAdmin(admin);
            serviceProviderList.add(serviceProvider);

            admin.setServiceProviders(serviceProviderList);
            adminRepository1.save(admin);
            serviceProviderRepository1.save(serviceProvider);
        }
        return  admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository1.findById(serviceProviderId);


        if (serviceProviderOptional.isPresent()) {
            ServiceProvider serviceProvider = serviceProviderOptional.get();


                CountryName validCountry = CountryName.valueOf(countryName.toUpperCase());
                Country country=null;
                try {
                    List<Country>countryList = countryRepository1.findAll();
                    for(Country c : countryList){
                        if(c.getCountryName().equals(validCountry)){
                            country = c;
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("Country not found");
                }
                country.setCode(validCountry.toCode());

                // Add country to service provider
                serviceProvider.getCountryList().add(country);
                serviceProviderRepository1.save(serviceProvider);

                return serviceProvider;
            }

        return null;
    }
}

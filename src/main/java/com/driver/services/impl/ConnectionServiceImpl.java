package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

       Optional<User> userOptional = userRepository2.findById(userId);
       User user = null;
       if(userOptional.isPresent()){
           user = userOptional.get();
       }
      List<Connection>userConnectionList =user.getConnectionList();
       if(user.getConnected()==true){
           throw  new Exception("Already connected");
       }
       else if (countryName.equals(user.getCountry().getCountryName())) {
           //This means that the user wants to connect to its original country, for which we do not require a connection.
           return user;
       }
       else{
           ServiceProvider serviceProvider = null;

           // Find the service provider for the given country using user's original country ID
           for (Connection connection : user.getCountry().getUser().getConnectionList()) {
               if (connection.getUser().getCountry().getCountryName().equals(countryName)) {
                   serviceProvider = connection.getServiceProvider();
                   break;
               }
           }

           if (serviceProvider == null) {
               throw new Exception("Unable to connect");
           }

           // Establish the connection
           String maskedIp = countryName.toUpperCase() + "." + serviceProvider.getId() + "." + userId;
           user.setMaskedIp(maskedIp);


           Connection connection = new Connection();
           connection.setServiceProvider(user.getServiceProviderList().get(0));
           connection.setUser(user);

           user.getConnectionList().add(connection);

           // Set user as connected
           user.setConnected(true);

           // Save the updated user
            userRepository2.save(user);
            connectionRepository2.save(connection);
       }
       return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {

        Optional<User> userOptional = userRepository2.findById(userId);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }
        List<Connection>userConnectionList =user.getConnectionList();
        if(user.getConnected()){
            throw  new Exception("Already disconnected");
        }

        user.setConnected(false);
        user.setMaskedIp(null);
        user.getConnectionList().clear();

        Connection connection = null;
        List<Connection> connectionList = connectionRepository2.findAll();
        for(Connection c : connectionList){
            if(c.getUser().equals(user)){
                connection = c;
            }
        }

       connection.setUser(null);
        connection.setServiceProvider(null);

        userRepository2.save(user);
        connectionRepository2.save(connection);

        return user;

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        Optional<User> senderOptional = userRepository2.findById(senderId);
        Optional<User> receiverOptional = userRepository2.findById(receiverId);

        User sender = null;
        User receiver = null;
        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
             sender = senderOptional.get();
             receiver = receiverOptional.get();

             Country currCountryOfReceiver = null;
             List<Connection> connectionList = connectionRepository2.findAll();
             for(Connection c : connectionList){
                 if(c.getUser().equals(receiver)){
                     currCountryOfReceiver = c.getUser().getCountry();
                 }
             }
             if(currCountryOfReceiver==null){
                 currCountryOfReceiver = receiver.getCountry();
             }

            if (!sender.getConnected()) {
                ServiceProvider suitableProvider = null;
                for (Connection connection : sender.getConnectionList()) {
                    if (connection.getUser().getCountry().getCountryName().equals(currCountryOfReceiver.getCountryName())) {
                        suitableProvider = connection.getServiceProvider();
                        break;
                    }
                }

                if (suitableProvider == null) {
                    throw new Exception("Cannot establish communication");
                }

            // Establish connection for sender
            String maskedIp = currCountryOfReceiver.getCountryName().toString().toUpperCase() +
                    "." + suitableProvider.getId() + "." + sender.getId();
            Connection connection = new Connection();
            sender.getConnectionList().add(connection);
            sender.setConnected(true);
            sender.setMaskedIp(maskedIp);
            connection.setServiceProvider(suitableProvider);
            userRepository2.save(sender);
            connectionRepository2.save(connection);
        } else {
                // Check if sender's original country matches receiver's current country
                if (!sender.getCountry().getCountryName().equals(currCountryOfReceiver.getCountryName())) {
                    throw new Exception("Cannot establish communication");
                }
            }
        }
        return sender;
    }
}

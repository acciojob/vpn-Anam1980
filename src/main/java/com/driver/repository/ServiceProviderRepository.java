package com.driver.repository;

import com.driver.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Integer> {
    @Query("SELECT sp FROM ServiceProvider sp " +
            "JOIN sp.connectionList conn " +
            "JOIN conn.user u " +
            "WHERE conn.user = :user AND u.originalCountry.countryName = :countryName")
    ServiceProvider findServiceProviderForCountry(@Param("countryName") String countryName, @Param("user") User user);

}


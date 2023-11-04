package com.driver.model;

import javax.persistence.*;
@Entity
public class Connection {
    public Connection() {
    }

    public Connection(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @ManyToOne
    @JoinColumn
    User user;

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn
    ServiceProvider serviceProvider;

}

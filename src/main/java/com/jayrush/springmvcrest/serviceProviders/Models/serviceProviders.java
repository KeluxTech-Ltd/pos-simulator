package com.jayrush.springmvcrest.serviceProviders.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

/**
 * @author JoshuaO
 */

@Entity
@Data
public class serviceProviders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String providerName;
    @OneToMany(cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH})
    private List<profiles> Profile;
    private boolean isSaved = false;
    private String savedDescription;
}

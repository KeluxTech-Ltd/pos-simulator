package com.jayrush.springmvcrest.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@MappedSuperclass

public abstract class AbstractEntity implements Serializable, SerializableEntity<AbstractEntity> {


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    @Version
    protected int version;

    protected String delFlag = "N";

    protected Date deletedOn;

    protected final Date createdOn = new Date();


    public Date getCreatedOn() {
        return createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Date getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = deletedOn;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEntity other = (AbstractEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String serialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(this);
        return data;
    }

    @Override
    public void deserialize(String data) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        AbstractEntity readValue = mapper.readValue(data, this.getClass());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(readValue, this);

    }

    @JsonIgnore
    public List<String> getDefaultSearchFields(){
        return new ArrayList<String>();
    };

    @Override
    public String toString() {
        return
                "id=" + id +
                        ", version=" + version +
                        ", delFlag='" + delFlag + '\'' +
                        ", deletedOn=" + deletedOn +
                        '}';
    }
}

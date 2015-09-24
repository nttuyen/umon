package com.nttuyen.android.umon.test.sqlite;

import com.nttuyen.android.umon.sqlite.Table;

import java.util.Date;

/**
 * Created by nttuyen on 9/24/15.
 */
@Table
public class MyEntity {
    private long id;
    private String name;
    private String description;
    private Date created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}

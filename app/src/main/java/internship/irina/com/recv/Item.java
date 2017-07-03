package internship.irina.com.recv;

import io.realm.RealmObject;

/**
 * Created by Irina on 6/29/2017.
 */

public class Item extends RealmObject{
    public String name, address;

    public Item() {

    }

    public Item(String name, String address) {

        this.name = name;
        this.address = address;
    }
    }

package contatos.esc.com.contatos.model;

import java.io.Serializable;

/**
 * Created by duda on 18/08/2016.
 */
public class Contato implements Serializable {
    private Integer Id;
    private String Name;
    private String Fone;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFone() {
        return Fone;
    }

    public void setFone(String fone) {
        Fone = fone;
    }
}

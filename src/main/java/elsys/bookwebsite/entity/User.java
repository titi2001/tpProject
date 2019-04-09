package elsys.bookwebsite.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    private int id;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

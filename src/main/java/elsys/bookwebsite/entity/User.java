package elsys.bookwebsite.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    private int id;
    private String password;
    private int booksLeft;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBooksLeft() {
        return booksLeft;
    }

    public void setBooksLeft(int booksLeft) {
        this.booksLeft = booksLeft;
    }
}

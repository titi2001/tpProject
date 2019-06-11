package elsys.bookwebsite.bookBindingModel;

public class UserBindingModel {
    private int id;
    private String password;
    private int booksLeft;
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

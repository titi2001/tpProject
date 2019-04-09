package elsys.bookwebsite.repository;

import elsys.bookwebsite.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Integer> {

}

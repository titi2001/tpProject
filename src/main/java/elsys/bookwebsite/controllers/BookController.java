package elsys.bookwebsite.controllers;

import elsys.bookwebsite.bookBindingModel.BookBindingModel;
import elsys.bookwebsite.entity.Book;
import elsys.bookwebsite.repository.BookRepository;
import elsys.bookwebsite.repository.UserRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class BookController {
    private static String UPLOADED_FOLDER = "E:\\Downloads\\bookwebsitee\\src\\main\\resources\\static\\";
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookController(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ModelAndView login(ModelAndView modelAndView) {

        return new ModelAndView("base-layout")
                .addObject("view", "user/login");
    }
    @PostMapping("/")
    public ModelAndView login(BookBindingModel model, @RequestParam("id") int id) throws IOException {
        if(this.userRepository.existsById(id)){
            if(id == 100000){
                return new ModelAndView("base-layout")
                        .addObject("view", "book/indexAdmin")
                        .addObject("books", this.bookRepository.findAll());}
            else{
                return new ModelAndView("base-layout")
                        .addObject("view", "book/index")
                        .addObject("books", this.bookRepository.findAll());
            }
        }
        else{
            return new ModelAndView("base-layout")
                    .addObject("view", "user/login");
        }
    }
    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {

        return new ModelAndView("base-layout")
                .addObject("view", "book/index")
                .addObject("books", this.bookRepository.findAll());
    }

    @GetMapping("/create")
    public ModelAndView create(ModelAndView modelAndView) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/create");
    }

    @PostMapping("/create")
    public String create(Book book, @RequestParam("file") MultipartFile file) throws IOException {

        Book t = new Book();
        t.setId(book.getId());
        t.setName(book.getName());
        t.setDescription(book.getDescription());
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        Files.write(path, bytes);
        t.setImage(file.getOriginalFilename());
        this.bookRepository.save(t);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView prepareDelete(@PathVariable Long id) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/delete")
                .addObject("book", this.bookRepository.findById(Math.toIntExact(id)).get());

    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/edit")
                .addObject("book", this.bookRepository.findById(Math.toIntExact(id)).get());

    }
    @GetMapping("/view/{id}")
    public ModelAndView vieww(@PathVariable Long id) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/view")
                .addObject("book", this.bookRepository.findById(Math.toIntExact(id)).get());

    }

    @PostMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id, BookBindingModel model) throws IOException  {
        Book book = this.bookRepository.findById(Math.toIntExact(id)).get();
        book.setName(model.getName());
        book.setDescription(model.getDescription());
        this.bookRepository.save(book);
        return new ModelAndView("redirect:/");
    }
    @GetMapping("/editImage/{id}")
    public ModelAndView editImage(@PathVariable Long id) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/editImage")
                .addObject("book", this.bookRepository.findById(Math.toIntExact(id)).get());

    }
    @PostMapping("/editImage/{id}")
    public ModelAndView editImage(@PathVariable Long id, BookBindingModel model, @RequestParam("file") MultipartFile file) throws IOException  {
        Book book = this.bookRepository.findById(Math.toIntExact(id)).get();
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        Files.write(path, bytes);
        book.setImage(file.getOriginalFilename());
        this.bookRepository.save(book);
        return new ModelAndView("redirect:/");
    }
    @GetMapping("/editFile/{id}")
    public ModelAndView editFile(@PathVariable Long id) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/editFile")
                .addObject("book", this.bookRepository.findById(Math.toIntExact(id)).get());

    }
    @PostMapping("/editFile/{id}")
    public ModelAndView editFile(@PathVariable Long id, BookBindingModel model, @RequestParam("file") MultipartFile file) throws IOException  {
        Book book = this.bookRepository.findById(Math.toIntExact(id)).get();
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        Files.write(path, bytes);
        book.setF(file.getOriginalFilename());
        this.bookRepository.save(book);
        return new ModelAndView("redirect:/");
    }


    @PostMapping("/delete/{id}")
    public ModelAndView processDelete(@PathVariable Long id) {
        this.bookRepository.deleteById(Math.toIntExact(id));
        return new ModelAndView("redirect:/");
    }
    @GetMapping("/download/{id}")
    public FileSystemResource downloadFile(@PathVariable Long id) {
        Book book = this.bookRepository.findById(Math.toIntExact(id)).get();
        return new FileSystemResource(new File(book.getF()));
    }
}

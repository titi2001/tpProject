package elsys.bookwebsite.controllers;

import elsys.bookwebsite.bookBindingModel.BookBindingModel;
import elsys.bookwebsite.bookBindingModel.UserBindingModel;
import elsys.bookwebsite.entity.Book;
import elsys.bookwebsite.entity.User;
import elsys.bookwebsite.repository.BookRepository;
import elsys.bookwebsite.repository.UserRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BookController {
    int loggedFlag = 0;
    private static String UPLOADED_FOLDER = "E:\\Downloads\\pleaseforfuckssake\\src\\main\\resources\\static\\";
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private User u;
    public BookController(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ModelAndView login(ModelAndView modelAndView) {
        if(loggedFlag == 0){
        return new ModelAndView("base-layout")
                .addObject("view", "user/login");}
        else if(loggedFlag == 1){
            return new ModelAndView("base-layout")
                    .addObject("view", "book/index")
                    .addObject("books", this.bookRepository.findAll());
        }
            return new ModelAndView("base-layout")
                    .addObject("view", "book/indexAdmin")
                    .addObject("books", this.bookRepository.findAll());

    }
    @PostMapping("/")
    public ModelAndView login(BookBindingModel model, @RequestParam("id") int id, @RequestParam("password") String password) throws IOException {
        if(this.userRepository.existsById(id)){
            User user = userRepository.findById(id).get();
            if(id == 100000){
                loggedFlag = 2;
                return new ModelAndView("base-layout")
                        .addObject("view", "book/indexAdmin")
                        .addObject("books", this.bookRepository.findAll());}
            else if(password.equals(new String(Base64.getDecoder().decode(user.getPassword())))){
                u = user;
                loggedFlag = 1;
                return new ModelAndView("base-layout")
                        .addObject("view", "book/index")
                        .addObject("books", this.bookRepository.findAll());}
            }
        return new ModelAndView("base-layout")
                .addObject("view", "user/login");
        }
    @GetMapping("/register")
    public ModelAndView reg(ModelAndView modelAndView) {

        return new ModelAndView("base-layout")
                .addObject("view", "user/register");
    }
    @PostMapping("/register")
    public ModelAndView register(UserBindingModel model, @RequestParam("id") int id, @RequestParam("password") String password) throws IOException {
        if(this.userRepository.existsById(id)){
            User user = userRepository.findById(id).get();
            if(id == 100000){
                return new ModelAndView("base-layout")
                        .addObject("view", "user/login");}
            else if(user.getPassword().equals("dwwefcwiwecmqow03")){
                user.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
                this.userRepository.save(user);
                return new ModelAndView("base-layout")
                        .addObject("view", "book/index")
                        .addObject("books", this.bookRepository.findAll());}
        }
        return new ModelAndView("base-layout")
                .addObject("view", "user/login");
    }

    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {

        return new ModelAndView("base-layout")
                .addObject("view", "book/index")
                .addObject("user", u)
                .addObject("books", this.bookRepository.findAll());
    }
    @PostMapping("/index")
    public ModelAndView search(ModelAndView modelAndView, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("tags") String tags, @RequestParam("genre") String genre) {
        List<Book> result = new ArrayList<>();
        List<Book> temp = bookRepository.findAll();
        for (Book b: temp) {
            if(title.length() > 0){
                if(b.getName().contains(title)){
                    result.add(b);
                }
            }
            if(description.length() > 0){
                if(b.getDescription().contains(description)){
                    result.add(b);
                }
            }
            if(tags.length() > 0){
                String[] r = tags.split(", ");
                if(b.getTags() != null){
                for(int i = 0; i < r.length; i++){
                if(b.getTags().contains(r[i])){
                    result.add(b);
                }}}
            }
            if(genre.length() > 0){
                String[] r = genre.split(", ");
                for(int i = 0; i < r.length; i++){
                    if(b.getGenre() != null){
                    if(b.getGenre().contains(r[i])){
                        result.add(b);
                    }}}
            }
        }
        return new ModelAndView("base-layout")
                .addObject("view", "book/index")
                .addObject("books", result);
    }

    @GetMapping("/create")
    public ModelAndView create(ModelAndView modelAndView) {
        return new ModelAndView("base-layout")
                .addObject("view", "book/create");
    }

    @PostMapping("/create")
    public String create(Book book,@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles) throws IOException {

        Book t = new Book();
        t.setId(book.getId());
        t.setName(book.getName());
        t.setDescription(book.getDescription());
        t.setGenre(book.getGenre());
        t.setTags(book.getTags());
        for(MultipartFile uploadedFile : uploadingFiles) {
            byte[] bytes = uploadedFile.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + uploadedFile.getOriginalFilename());
            Files.write(path, bytes);
            if(uploadedFile.getOriginalFilename().contains(".png") || uploadedFile.getOriginalFilename().contains(".jpg")){
                t.setImage(uploadedFile.getOriginalFilename());
            }
            else{
                t.setF(uploadedFile.getOriginalFilename());
            }
        }
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
        book.setGenre(book.getGenre());
        book.setTags(book.getTags());
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
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException, URISyntaxException {
        if(u.getBooksLeft() > 0){
            u.setBooksLeft(u.getBooksLeft()-1);
            userRepository.save(u);
        }
        else{
            URI yahoo = new URI("redirect:/");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(yahoo);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
            }
        Book book = this.bookRepository.findById(Math.toIntExact(id)).get();
        File file = new File(UPLOADED_FOLDER + "/" + book.getF());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(file.length()) //
                .body(resource);
    }
    @PostMapping("/download/{id}")
    public ModelAndView editFile(){
        return new ModelAndView("redirect:/");
    }
}

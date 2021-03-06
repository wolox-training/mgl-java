package wolox.training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.BookIdMismatchException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

/**
 * Controller for Books
 *
 * @author M. G.
 */

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OpenLibraryService openLibraryService;

    /**
     * Greet a person.
     *
     * @param name the name of the person
     * @return the name of the view rendered
     */

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name = "name", required = false, defaultValue = "World") String name,
        Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    /**
     * Find all {@link Book}s by some criteria.
     *
     * @param id        the id of the books
     * @param genre     the genre of the books
     * @param author    the author of the books
     * @param image     the image of the books
     * @param title     the title of the books
     * @param subtitle  the subtitle of the books
     * @param publisher the publisher of the books
     * @param year      the year of the books
     * @param pages     the pages of the books
     * @param isbn      the isbn of the books
     * @param pageable  the parameters about pagination and sorting of the books
     * @return all the books that are persisted and match the criteria
     */

    @GetMapping
    public Iterable findAll(@RequestParam(required = false) Long id,
        @RequestParam(defaultValue = "") String genre,
        @RequestParam(required = false) String author, @RequestParam(required = false) String image,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String subtitle,
        @RequestParam(required = false) String publisher,
        @RequestParam(required = false) String year,
        @RequestParam(required = false) Integer pages,
        @RequestParam(required = false) String isbn, Pageable pageable) {
        return bookRepository
            .findByAllFields(id, genre, author, image, title, subtitle, publisher, year, pages,
                isbn, pageable);
    }

    /**
     * Find a {@link Book}.
     *
     * @param id the id of the book
     * @return the book found or an exception otherwise
     */

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
    }

    /**
     * Search a {@link Book} in the database. If it does not exist, search in the OpenLibrary
     * service and persist the book.
     *
     * @param isbn the ISBN of the book
     * @return the book found or an exception otherwise
     */

    @GetMapping("/search/{isbn}")
    public ResponseEntity<Book> search(@PathVariable String isbn) {
        return openLibraryService.search(isbn);
    }

    /**
     * Create a {@link Book}.
     *
     * @param book the book to be created
     * @return the book persisted
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * Delete a {@link Book}.
     *
     * @param id the id of the book to be deleted
     */

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    /**
     * Update a {@link Book}.
     *
     * @param book the details to be updated of the book
     * @param id   the id of the book to be updated
     * @return the book persisted
     */

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException();
        }
        bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
        return bookRepository.save(book);
    }
}

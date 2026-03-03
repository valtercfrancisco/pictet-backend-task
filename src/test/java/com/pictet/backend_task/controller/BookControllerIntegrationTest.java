package com.pictet.backend_task.controller;

import com.pictet.backend_task.model.Category;
import com.pictet.backend_task.repository.BookRepository;
import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.repository.model.Difficulty;
import com.pictet.backend_task.repository.model.Section;
import com.pictet.backend_task.repository.model.SectionType;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Book Controller Integration Tests")
class BookControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        bookRepository.deleteAll();

        // Create test book with sections
        testBook = new Book();
        testBook.setTitle("The Crystal Caverns");
        testBook.setAuthor("Evelyn Stormrider");
        testBook.setDifficulty(Difficulty.EASY);

        val section1 = new Section();
        section1.setId(1);
        section1.setText("You stand at the entrance of the legendary Crystal Caverns.");
        section1.setType(SectionType.BEGIN);
        section1.setOptions(new ArrayList<>());
        section1.setBook(testBook);

        val section2 = new Section();
        section2.setId(2);
        section2.setText("You reach the end of your journey.");
        section2.setType(SectionType.END);
        section2.setOptions(new ArrayList<>());
        section2.setBook(testBook);

        testBook.setSections(List.of(section1, section2));

        val categories = new HashSet<Category>();
        categories.add(Category.FANTASY);
        testBook.setCategories(categories);

        testBook = bookRepository.save(testBook);
    }

    @Test
    @DisplayName("GET /v1/books - Should return all books")
    void getAllBooks_ShouldReturnAllBooks() throws Exception {
        mockMvc.perform(get("/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Crystal Caverns")))
                .andExpect(jsonPath("$[0].author", is("Evelyn Stormrider")))
                .andExpect(jsonPath("$[0].difficulty", is("EASY")))
                .andExpect(jsonPath("$[0].categories", hasSize(1)))
                .andExpect(jsonPath("$[0].categories[0]", is("FANTASY")))
                .andExpect(jsonPath("$[0].sections", hasSize(2)));
    }

    @Test
    @DisplayName("GET /v1/books/{id} - Should return book by ID")
    void getBookById_WithValidId_ShouldReturnBook() throws Exception {
        mockMvc.perform(get("/v1/books/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("The Crystal Caverns")))
                .andExpect(jsonPath("$.author", is("Evelyn Stormrider")))
                .andExpect(jsonPath("$.difficulty", is("EASY")))
                .andExpect(jsonPath("$.sections", hasSize(2)))
                .andExpect(jsonPath("$.sections[0].type", is("BEGIN")))
                .andExpect(jsonPath("$.sections[1].type", is("END")));
    }

    @Test
    @DisplayName("GET /v1/books/{id} - Should return 404 for non-existent book")
    void getBookById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/v1/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Book Not Found")))
                .andExpect(jsonPath("$.message", is("Book with id 999 not found")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /v1/books/search/title - Should search books by title")
    void searchByTitle_ShouldReturnMatchingBooks() throws Exception {
        mockMvc.perform(get("/v1/books/search/title")
                        .param("title", "Crystal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Crystal Caverns")));
    }

    @Test
    @DisplayName("GET /v1/books/search/title - Should be case-insensitive")
    void searchByTitle_ShouldBeCaseInsensitive() throws Exception {
        mockMvc.perform(get("/v1/books/search/title")
                        .param("title", "crystal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Crystal Caverns")));
    }

    @Test
    @DisplayName("GET /v1/books/search/author - Should search books by author")
    void searchByAuthor_ShouldReturnMatchingBooks() throws Exception {
        mockMvc.perform(get("/v1/books/search/author")
                        .param("author", "Evelyn"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].author", is("Evelyn Stormrider")));
    }

    @Test
    @DisplayName("GET /v1/books/search/difficulty - Should search books by difficulty")
    void searchByDifficulty_ShouldReturnMatchingBooks() throws Exception {
        mockMvc.perform(get("/v1/books/search/difficulty")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficulty", is("EASY")));
    }

    @Test
    @DisplayName("GET /v1/books/search/difficulty - Should return 400 for invalid difficulty")
    void searchByDifficulty_WithInvalidDifficulty_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/v1/books/search/difficulty")
                        .param("difficulty", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Request")))
                .andExpect(jsonPath("$.message", is("Difficulty must be one of: EASY, MEDIUM, HARD")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /v1/books/search/category - Should search books by category")
    void searchByCategory_ShouldReturnMatchingBooks() throws Exception {
        mockMvc.perform(get("/v1/books/search/category")
                        .param("category", "FANTASY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categories", hasItem("FANTASY")));
    }

    @Test
    @DisplayName("GET /v1/books/search/category - Should return 400 for invalid category")
    void searchByCategory_WithInvalidCategory_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/v1/books/search/category")
                        .param("category", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Request")))
                .andExpect(jsonPath("$.message", containsString("Category must be one of:")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /v1/books/{id}/categories/{category} - Should add category to book")
    void addCategoryToBook_WithValidCategory_ShouldAddCategory() throws Exception {
        mockMvc.perform(post("/v1/books/{id}/categories/{category}", testBook.getId(), "HORROR"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.categories", hasSize(2)))
                .andExpect(jsonPath("$.categories", hasItem("FANTASY")))
                .andExpect(jsonPath("$.categories", hasItem("HORROR")));
    }

    @Test
    @DisplayName("POST /v1/books/{id}/categories/{category} - Should return 400 for invalid category")
    void addCategoryToBook_WithInvalidCategory_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/v1/books/{id}/categories/{category}", testBook.getId(), "INVALID_CATEGORY"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Request")))
                .andExpect(jsonPath("$.message", containsString("Category must be one of:")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /v1/books/{id}/categories/{category} - Should return 404 for non-existent book")
    void addCategoryToBook_WithInvalidBookId_ShouldReturn404() throws Exception {
        mockMvc.perform(post("/v1/books/{id}/categories/{category}", 999L, "HORROR"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Book Not Found")))
                .andExpect(jsonPath("$.message", is("Book with id 999 not found")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("DELETE /v1/books/{id}/categories/{category} - Should remove category from book")
    void removeCategoryFromBook_WithExistingCategory_ShouldRemoveCategory() throws Exception {
        // First add a category
        mockMvc.perform(post("/v1/books/{id}/categories/{category}", testBook.getId(), "HORROR"))
                .andExpect(status().isOk());

        // Then remove it
        mockMvc.perform(delete("/v1/books/{id}/categories/{category}", testBook.getId(), "HORROR"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.categories", hasSize(1)))
                .andExpect(jsonPath("$.categories[0]", is("FANTASY")));
    }

    @Test
    @DisplayName("DELETE /v1/books/{id}/categories/{category} - Should return 400 for non-existent category")
    void removeCategoryFromBook_WithNonExistentCategory_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/v1/books/{id}/categories/{category}", testBook.getId(), "SCIENCE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Request")))
                .andExpect(jsonPath("$.message", is("Category SCIENCE not found on book with id %d".formatted(testBook.getId()))))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("DELETE /v1/books/{id}/categories/{category} - Should return 404 for non-existent book")
    void removeCategoryFromBook_WithInvalidBookId_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/v1/books/{id}/categories/{category}", 999L, "FANTASY"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Book Not Found")))
                .andExpect(jsonPath("$.message", is("Book with id 999 not found")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

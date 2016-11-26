# API Gateway JAX-RS

This is a simple proof of concept JAX-RS subset implementation for API
Gateway using a Java handler. Ideally, a Java handler can be written
using Standard JAX-RS binding, making it a WS implementation completely
transparent to the delivery method.

## Example code

Define your resource as necessary:

```
@Path("books")
public class BookResource {
	@Context
	DynamoDB db;

	Table books;

	@PostConstruct
	public void init() {
		this.books = db.getTable("books");
	}

	@GET
	public List<Book> getBooks() {
		List<Book> bs = new ArrayList<>();
		for (Item item : books.scan()) {
			bs.add(fromItem(item));
		}
		return bs;
	}

	@GET
	@Path("{id}")
	public Book getBook(@PathParam("id") String isbn) {
		Item item = books.getItem("isbn", isbn);
		return Optional.ofNullable(item).map(this::fromItem).orElse(null);
	}

	@POST
	public Response createOrUpdateBook(Book book) {
		books.updateItem(new Item()
			.withString("isbn", book.getISBN())
			.withString("title", book.getTitle())
			.withString("author", book.getAuthor()));
		return Response.accepted();
	}

	private Book fromItem(Item item) {
		return new Book()
			.withISBN(item.getString("isbn"))
			.withTitle(item.getString("title"))
			.withAuthor(item.getString("author"));
	}
}
```

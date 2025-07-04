public class Note {
    private int id;
    private String name;
    private int price;
    private int amount;
    private String date;
    private String description;

    public Note(int id,String name, int price, int amount, String date, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getAmount() { return amount; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}

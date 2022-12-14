package entity;

public enum Category {
    PIZZA(1, "Pizza"),
    SUSHI(2, "Sushi"),
    BURGER(3, "Burger"),
    DRINK(4, "Drink"),
    SALAD(5, "Salad"),
    DESSERT(6, "Dessert");
    private final int id;
    private final String name;

    Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category getCategoryById(int id) {
        for (Category c : values()) {
            if (c.id == id) {
                return c;
            }
        }
        return null;
    }

    public static Category getCategoryByName(String name) {
        for (Category c : values()) {
            if (c.name.equals(name)) {
                return c;
            }

        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
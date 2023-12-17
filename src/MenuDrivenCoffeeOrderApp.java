import java.util.*;

// Singleton Pattern
class CoffeeShop {
    private static CoffeeShop instance;

    private CoffeeShop() {
    }

    public static CoffeeShop getInstance() {
        if (instance == null) {
            instance = new CoffeeShop();
        }
        return instance;
    }

    public void takeOrder(String customerName, Coffee coffee) {
        System.out.println(customerName + " ordered a " + coffee.getDescription());
    }
}

// Factory Pattern
interface CoffeeFactory {
    Coffee createCoffee();
}

class EspressoFactory implements CoffeeFactory {
    @Override
    public Coffee createCoffee() {
        return new Espresso();
    }
}

class LatteFactory implements CoffeeFactory {
    @Override
    public Coffee createCoffee() {
        return new Latte();
    }
}

// Topping Interface
interface Topping {
    String getToppingDescription();
}

class WhippedCream implements Topping {
    @Override
    public String getToppingDescription() {
        return "Whipped Cream";
    }
}

class Caramel implements Topping {
    @Override
    public String getToppingDescription() {
        return "Caramel";
    }
}

// Coffee Interface
interface Coffee {
    String getDescription();

    List<Topping> getToppings();
}

// Concrete Coffee Implementations
class Espresso implements Coffee {
    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public List<Topping> getToppings() {
        return Collections.emptyList();
    }
}

class Latte implements Coffee {
    @Override
    public String getDescription() {
        return "Latte";
    }

    @Override
    public List<Topping> getToppings() {
        return Collections.emptyList();
    }
}

// Updated Espresso and Latte Classes
class EspressoWithToppings extends Espresso {
    private List<Topping> toppings;

    public EspressoWithToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    @Override
    public String getDescription() {
        StringBuilder description = new StringBuilder(super.getDescription());
        for (Topping topping : toppings) {
            description.append(" with ").append(topping.getToppingDescription());
        }
        return description.toString();
    }

    @Override
    public List<Topping> getToppings() {
        return toppings;
    }
}

class LatteWithToppings extends Latte {
    private List<Topping> toppings;

    public LatteWithToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    @Override
    public String getDescription() {
        StringBuilder description = new StringBuilder(super.getDescription());
        for (Topping topping : toppings) {
            description.append(" with ").append(topping.getToppingDescription());
        }
        return description.toString();
    }

    @Override
    public List<Topping> getToppings() {
        return toppings;
    }
}

// Adapter Pattern
class CoffeeMachine {
    public void brew(String customerName, Coffee coffee, List<Topping> toppings) {
        System.out.println("Brewing " + coffee.getDescription() + " for " + customerName);

        if (!toppings.isEmpty()) {
            System.out.print("Adding toppings: ");
            for (Topping topping : toppings) {
                System.out.print(topping.getToppingDescription() + " ");
            }
            System.out.println();
        }
    }
}

// Facade Pattern
class CoffeeOrderFacade {
    private CoffeeMachine coffeeMachine;

    public CoffeeOrderFacade() {
        this.coffeeMachine = new CoffeeMachine();
    }

    public void placeOrder(String customerName, Coffee coffee, List<Topping> toppings) {
        CoffeeShop.getInstance().takeOrder(customerName, coffee);
        coffeeMachine.brew(customerName, coffee, toppings);
    }
}

// Command Pattern
interface Command {
    void execute();
}

class CoffeeOrderCommand implements Command {
    private String customerName;
    private Coffee coffee;
    private List<Topping> toppings;

    public CoffeeOrderCommand(String customerName, Coffee coffee, List<Topping> toppings) {
        this.customerName = customerName;
        this.coffee = coffee;
        this.toppings = toppings;
    }

    @Override
    public void execute() {
        CoffeeOrderFacade facade = new CoffeeOrderFacade();
        facade.placeOrder(customerName, coffee, toppings);
    }
}

// Menu Command
class MenuCommand implements Command {
    private Map<String, CoffeeFactory> coffeeFactories;
    private Scanner scanner;

    public MenuCommand(Map<String, CoffeeFactory> coffeeFactories, Scanner scanner) {
        this.coffeeFactories = coffeeFactories;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        while (true) {
            System.out.println("Choose a coffee type (espresso/latte) or type 'exit' to go back to the main menu: ");
            String coffeeType = scanner.nextLine();

            if (coffeeType.equalsIgnoreCase("exit")) {
                break;
            }

            CoffeeFactory coffeeFactory = coffeeFactories.get(coffeeType.toLowerCase());
            if (coffeeFactory != null) {
                Coffee coffee = coffeeFactory.createCoffee();

                // Ask for toppings
                System.out.println("Enter toppings separated by commas (e.g., whipped cream, caramel): ");
                String toppingsInput = scanner.nextLine();
                List<Topping> toppings = parseToppings(toppingsInput);

                // Create Coffee with Toppings
                Coffee coffeeWithToppings;
                if (coffee != null && !toppings.isEmpty()) {
                    if (coffee instanceof Espresso) {
                        coffeeWithToppings = new EspressoWithToppings(toppings);
                    } else {
                        coffeeWithToppings = new LatteWithToppings(toppings);
                    }
                } else {
                    coffeeWithToppings = coffee;
                }

                // Create command with different coffee orders
                Command orderCommand = new CoffeeOrderCommand("Customer", coffeeWithToppings, toppings);
                orderCommand.execute();
            } else {
                System.out.println("Invalid coffee type. Try again.");
            }
        }
    }

    // Helper method to parse toppings
    private List<Topping> parseToppings(String toppingsInput) {
        List<Topping> toppings = new ArrayList<>();
        String[] toppingsArray = toppingsInput.split(",");
        for (String topping : toppingsArray) {
            topping = topping.trim().toLowerCase();
            switch (topping) {
                case "whipped cream":
                    toppings.add(new WhippedCream());
                    break;
                case "caramel":
                    toppings.add(new Caramel());
                    break;
                // Add more toppings as needed
                default:
                    System.out.println("Invalid topping: " + topping);
            }
        }
        return toppings;
    }
}

// Invoker
class CoffeeShopInvoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }
}

public class MenuDrivenCoffeeOrderApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, CoffeeFactory> coffeeFactories = new HashMap<>();
        coffeeFactories.put("espresso", new EspressoFactory());
        coffeeFactories.put("latte", new LatteFactory());

        CoffeeShopInvoker coffeeShopInvoker = new CoffeeShopInvoker();

        while (true) {
            System.out.println("welcome to make your choice");
            System.out.println("1. Order Coffee");
            System.out.println("2. Exit");
            System.out.println("Choose an action:");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    Command menuCommand = new MenuCommand(coffeeFactories, scanner);
                    coffeeShopInvoker.setCommand(menuCommand);
                    coffeeShopInvoker.executeCommand();
                    break;
                case 2:
                    System.out.println("Exiting Coffee Shop. Thank you!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

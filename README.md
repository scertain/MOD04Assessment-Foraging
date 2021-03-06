# MOD04Assessment-Foraging


## Working Features
All of the following features were reported as "completed" by the previous development team, but early application testers have been complaining that some things aren’t working correctly or might actually be missing. Review the functionality of each of the following features and fix any issues that you find:

1. Add an Item.
2. View Items.
3. View Foragers.
4. Add a Forage.
5. View Forages by date.

## Incomplete Features
The following features are not functional, though there may be existing application components that can be used to implement them.

1. Add a Forager. [Forager = a person]
2. Create a report that displays the kilograms of each Item collected on one day.
3. Create a report that displays the total value of each Category collected on one day.
4. Found Errors/Bugs
    * 

# Requirements

## Items
1. Name is required.
2. Name cannot be duplicated.
3. Category is required.
4. Dollars/Kg is required.
5. Dollars/Kg must be between $0 (inedible, poisonous) and $7500.
6. Item ID is a system-generated unique sequential integer.

## Foragers
1. First name is required.
2. Last name is required.
3. State is required.
4. The combination of first name, last name, and state cannot be duplicated.
5. Forager ID is a system-generated GUID (globally unique identifier).

## Forages
1. Item is required and must exist.
2. Forager is required and must exist.
3. Date is required and must not be in the future.
4. Kilograms must be a positive number not more than 250.
5. The combination of date, Item, and Forager cannot be duplicated. (Can't forage the same item on the same day. It should be tracked as one Forage.)
6. Forage ID is a system-generated GUID (globally unique identifier).

## Technical Requirements
* In addition to application features, please add Spring dependency injection to the project. You may configure Spring DI with either XML or annotations.

* Generate the kilogram/item report with streams and total value/category report with loops and intermediate variables, but keep the ultimate goal in mind: using data to create accurate reports. If one approach gives you too much trouble, use the other to solve the data problem.

* All financial math must use BigDecimal.

* Dates must be LocalDate, never strings.

## File Format
* Foragers and Items are stored in comma-delimited files with a header. You may not change the delimiter or alter the header. If possible, prevent commas from being added to data. An extra comma will prevent the repositories from reading the record.

* Forage data is stored in what we call an "unfortunate decision". Each day's data is stored in a separate file with the naming convention: yyyy-MM-dd.csv.

### Examples
    * Forages for 15-July-2020 are stored in 2020-07-15.csv.
    * Forages for 31-Oct-2019 are stored in 2019-10-31.csv.
    * Forages for 1-Jan-2019 are stored in 2019-01-01.csv.

* Forage files are comma-delimited with a header. You may not change any aspect of the Forage data files. It is an inconvenient file format, but it's what the client wanted.

### Testing
* All new features must be thoroughly tested. You are not responsible for creating testing for existing features unless you find a bug. If you find a bug, please create a test to confirm the expected behavior and prevent regressions.

## Approach
* Plan before you write code.
* We expect that you will have questions. In fact, you should have questions. Please direct them to your Project Manager (your Instructor). Take time to understand the code before you write new code. Run the application. Trace its execution. Draw pictures.

## Stretch Goals
* If time allows, the client would love to get started on Version 2 features.
* They include:
    * Update an existing Item.
    * Delete an Item. (Careful with this one. If an Item is deleted, one of two things should happen: 1. All Forages that include that Item should also be deleted. Forages without a valid Item are not allowed. 2. Make it a "soft" delete. Add an Item status and hide all Items with a deleted status from search results.)
    * Update a Forager.
    * Delete a Forager. (See Item deletion.)
    * Update a Forage.
    * Delete a Forage.
    * Figure out a way to view Forages by Forager. This is a non-trivial change.
    * Add reports: total value per Forager, Item kilograms collected per Forager


# Code Snippets

## App.main()
    public class App {
        public static void main(String[] args) {

        ConsoleIO io = new ConsoleIO();
          // captures user input via the console.
        View view = new View(io);
          // the view takes in the user input captured in io

        ForageFileRepository forageFileRepository = new ForageFileRepository("./data/forage_data");
          // sets path for forageFileRepo in subdir under 'data'; no filenames here as each day's file serves as a data file.
        ForagerFileRepository foragerFileRepository = new ForagerFileRepository("./data/foragers.csv");
          // sets the name of the forager data file.
        ItemFileRepository itemFileRepository = new ItemFileRepository("./data/items.txt");
          // sets the name of the foraged items data file.

        ForagerService foragerService = new ForagerService(foragerFileRepository);
          // instantiates new ForagerService that takes in the path to the frageFileRepo.
        ForageService forageService = new ForageService(forageFileRepository, foragerFileRepository, itemFileRepository);
          // instantiates new ForageService that takes in forageFileRepo, the forager data file, & the item data file.
        ItemService itemService = new ItemService(itemFileRepository);
          // instantiates new itemService that takes in the item data file.

        Controller controller = new Controller(foragerService, forageService, itemService, view);
          // instantiates new Controller that takes in the forageService, foragerService, itemService, & the view.
        controller.run();
        }
    }
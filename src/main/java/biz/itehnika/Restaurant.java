package biz.itehnika;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Restaurant {

    static EntityManagerFactory emf;
    static EntityManager em;
    static CriteriaBuilder builder;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        emf = Persistence.createEntityManagerFactory("biz.itehnika.Restaurant");
        em = emf.createEntityManager();
        builder = em.getCriteriaBuilder();

        try {

           while (true){
               System.out.println("1 => Add new dish");
               System.out.println("2 => Show menu (all dishes)");
               System.out.println("3 => Show menu by name");
               System.out.println("4 => Show menu by price");
               System.out.println("5 => Show dishes with discount");
               System.out.println("6 => Show sets of dishes less than 1 kg");
               System.out.println("7 => Delete dish");
               System.out.println("0 => Generate random menu (for test)");
               System.out.println("'Enter' => Exit");

               String str = scanner.nextLine();

               switch (str){
                   case "1":    // Add new dish
                       addDishToMenu(scanner);
                       break;
                   case "2":    // Show menu
                       showMenu(true);
                       break;
                   case "3":    // Show menu by name
                       showMenuByName(scanner);
                       break;
                   case "4":    // Show menu by price
                       showMenuByPrice(scanner);
                       break;
                   case "5":    // Show dishes with discount
                       showMenuByDiscount();
                       break;
                   case "6":    // Show sets of dishes less than 1 kg
                       showSetsOfDishesLess1Kilo();
                       break;
                   case "7":     // Delete dish
                       deleteDishFromMenu(scanner);
                       break;
                   case "0":    // Generate random menu (for test)
                       generateRandomMenu(scanner);
                       break;
                   default:
                       return;
               }
           }
        }finally {
            em.close();
            emf.close();
            scanner.close();
        }
    }

    public static void addDishToMenu(Scanner scanner){
        System.out.println("Input name of the dish:");
        String dishName = scanner.nextLine();
        System.out.println("Input price of the dish:");
        int dishPrice = Integer.parseInt(scanner.nextLine());
        System.out.println("Input weight of the dish:");
        int dishWeight = Integer.parseInt(scanner.nextLine());
        System.out.println("Is there discount for this dish? (Y/N):");
        boolean dishDiscount = scanner.nextLine().equalsIgnoreCase("y");
        Menu dish = new Menu(dishName, dishPrice, dishWeight, dishDiscount);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.persist(dish);
            transaction.commit();
            System.out.println("The dish was added");
            System.out.println();
        }catch (Exception e){
            transaction.rollback();
        }
        em.clear();
    }

    public static List<Menu> showMenu(boolean isPrinting){
        TypedQuery<Menu> query = em.createNamedQuery("ShowMenu", Menu.class);
        List<Menu> menuList = query.getResultList();
        if (menuList.isEmpty()){
            System.out.println("There are no dishes in menu!");
            System.out.println();
            return null;
        }
        if (isPrinting){
            for (Menu dish : menuList){
                System.out.println(dish);
            }
        }
        System.out.println();
        return menuList;
    }

    public static void showMenuByName(Scanner scanner){
        System.out.println("Input the name (part of name) of dish:");
        String nameOfDish = "%" + scanner.nextLine() + "%";
        CriteriaQuery<Menu> crQuery = builder.createQuery(Menu.class);
        Root<Menu> root = crQuery.from(Menu.class);
        crQuery.select(root).where(builder.like(root.get("name"), nameOfDish));
        TypedQuery<Menu> query = em.createQuery(crQuery);
        List<Menu> menuList = query.getResultList();
        if (menuList.isEmpty()){
            System.out.println("There are no dishes with name *" + nameOfDish + "* in menu!");
            System.out.println();
            return;
        }
        for (Menu dish : menuList){
            System.out.println(dish);
        }
        System.out.println();
    }

    public static void showMenuByPrice(Scanner scanner){
        System.out.println("Input price 'From':");
        int priceFrom = Integer.parseInt(scanner.nextLine());
        System.out.println("Input price 'To':");
        int priceTo = Integer.parseInt(scanner.nextLine());
        CriteriaQuery<Menu> crQuery = builder.createQuery(Menu.class);
        Root<Menu> root = crQuery.from(Menu.class);
        crQuery.select(root).where(builder.between(root.get("price"), priceFrom, priceTo));
        TypedQuery<Menu> query = em.createQuery(crQuery);
        List<Menu> menuList = query.getResultList();
        if (menuList.isEmpty()){
            System.out.println("There are no dishes with price from " + priceFrom + " to " + priceTo + " in menu!");
            System.out.println();
            return;
        }
        for (Menu dish : menuList){
            System.out.println(dish);
        }
        System.out.println();
    }

    public static void showMenuByDiscount(){
        CriteriaQuery<Menu> crQuery = builder.createQuery(Menu.class);
        Root<Menu> root = crQuery.from(Menu.class);
        crQuery.select(root).where(builder.equal(root.get("discount"), true));
        TypedQuery<Menu> query = em.createQuery(crQuery);
        List<Menu> menuList = query.getResultList();
        if (menuList.isEmpty()){
            System.out.println("There are no dishes with discount in menu!");
            System.out.println();
            return;
        }
        for (Menu dish : menuList){
            System.out.println(dish);
        }
        System.out.println();
    }

    public static void showSetsOfDishesLess1Kilo(){
        List<Menu> menuList = showMenu(false);
        if (menuList.isEmpty()){
            System.out.println("There are no dishes in menu!");
            return;
        }
        List<List<Menu>> setsLess1Kilo = new ArrayList<>();
        int currentWeight = 0;
        int maxWeight = 1000;

        for (int j = 0; j < menuList.size(); j++){
            List<Menu> nextDishSet = new ArrayList<>();
            for (int i = j; i < menuList.size(); i++){
                if (currentWeight + menuList.get(i).getWeight() < maxWeight){
                    currentWeight += menuList.get(i).getWeight();
                    nextDishSet.add(menuList.get(i));
                }else continue;
            }
            setsLess1Kilo.add(nextDishSet);
            currentWeight = 0;
        }
        System.out.println("Set(s) of dishes less tan 1 kilo:");
        for (List<Menu> dishSet : setsLess1Kilo){
            int weight = 0;
            for (Menu dish : dishSet){
                System.out.println(dish);
                weight += dish.getWeight();
            }
            System.out.println("Weight of set = " + weight);
            System.out.println();
        }
   }

    public static void deleteDishFromMenu(Scanner scanner){
        System.out.println("Input 'Id' of the dish for delete:");
        int dishId = Integer.parseInt(scanner.nextLine());
        Menu dish = em.getReference(Menu.class, dishId);
        if (dish == null){
            System.out.println("The dish with 'Id' =  " + dishId + " is not found!");
            System.out.println();
            return;
        }
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.remove(dish);
            transaction.commit();
            System.out.println("The dish with 'Id' =  " + dishId + " deleted!");
            System.out.println();
        } catch (Exception e){
            transaction.rollback();
        }
        em.clear();
    }

    public static void generateRandomMenu(Scanner scanner){
        System.out.println("Input number of dishes:");
        int numOfItem = Integer.parseInt(scanner.nextLine());
        List<Menu> menuList = new ArrayList<>();
        for (int i =0; i < numOfItem; i++){
            menuList.add(new Menu(Dishes.getRandomName(),
                                  Dishes.getRandomPrice(),
                                  Dishes.getRandomWeight(),
                                  Dishes.getRandomDiscount())
                        );
        }
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            for (Menu dish : menuList){
                em.persist(dish);
            }
            transaction.commit();
            System.out.println(numOfItem + " random dishes added");
            System.out.println();
        }catch (Exception e){
            transaction.rollback();
        }
        em.clear();
    }
}
package tests;

public class TestCarte {
    public static void main(String[] args) {
        int test = (int)Math.random();
        System.out.printf("%d \n", test);

        // define the range 
        int max = 10; 
        int min = 1; 
        int range = max - min + 1; 
  
        // generate random numbers within 1 to 10 
        int rand = (int)(Math.random() * range) + min; 
  
            // Output is different everytime this code is executed 
        System.out.println(rand); 
    }
}

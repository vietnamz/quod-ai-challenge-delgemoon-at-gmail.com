/**
 * The type Main.
 */
public class Main {

    /**
     * Just start to see java docwork.
     *
     * @param hello the hello
     */
    public void justStartToSeeJavaDocwork(String hello) {
        System.out.println(hello);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        ((Runnable) () -> System.out.println("Hello World")).run();
    }
}

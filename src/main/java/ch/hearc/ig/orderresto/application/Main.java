package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;

public class Main {

  public static void main(String[] args) {

    (new MainCLI()).run();

//    AbstractCLIScreen currentScreen = new MainScreen();
//    while (currentScreen != null) {
//      currentScreen = currentScreen.execute();
//    }
//    var scanner = new Scanner(System.in);
//    var fakeItems = new FakeDb();
//    var printStream = System.out;
//    var cli = new CLI(scanner, printStream, fakeItems);
//
//    cli.start();
  }
}

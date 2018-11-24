package Client;


public class Menu{

    private int option;

    public void setVisible() {
        switch (option) {
            case 1:
                System.out.println("************* MENU ****************\n" +
                        "* 1 - Log In                      *\n" +
                        "* 2 - Sign In                     *\n" +
                        "* 0 - Exit                        *\n" +
                        "***********************************\n");
                break;
            }
        }
}

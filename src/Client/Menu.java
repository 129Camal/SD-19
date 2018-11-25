package Client;

public class Menu {

    private int menuOption;

    public void setVisible() {
        switch (menuOption) {
            case 1:
                System.out.println("************* MENU ****************\n" +
                        "* 1 - Log In                      *\n" +
                        "* 2 - Sign In                     *\n" +
                        "* 0 - Exit                        *\n" +
                        "***********************************\n");
                break;

            case 2: System.out.println("************* MENU ****************\n"+
                    "* 1 - Acquire Server                 *\n"+
                    "* 2 - Auctions                       *\n"+
                    "* 0 - Exit                           *\n"+
                    "**************************************\n");
                break;
        }
    }

    public int getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(int menuOption) {
        this.menuOption = menuOption;
    }
}